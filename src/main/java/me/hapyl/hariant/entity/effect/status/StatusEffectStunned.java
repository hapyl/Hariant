package me.hapyl.hariant.entity.effect.status;

import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.entity.effect.EffectType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.Cancel;
import me.hapyl.hariant.event.HariantAttackEvent;
import me.hapyl.hariant.event.HariantDamageEvent;
import me.hapyl.hariant.event.HariantTalentPreconditionEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class StatusEffectStunned extends StatusEffectImpl implements Listener {
    
    private static final Component TITLE = Component.text("sᴛᴜɴɴᴇᴅ", Colors.WHITE, TextDecoration.BOLD);
    
    private final double damageMultiplier = 1.5;
    
    StatusEffectStunned() {
        super(Key.ofString("effect_stunned"), Component.text("Stunned"), EffectType.DEBUFF);
    }
    
    @Override
    public void onApply(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int duration) {
        entity.setSitting(LocationHelper.anchor(entity.getLocation()), false);
        
        // Fx
        entity.playWorldSound(Sound.BLOCK_COPPER_CHEST_CLOSE, 0.75f);
    }
    
    @Override
    public void onTick(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int tick) {
        entity.sendTitleSubtitle(TITLE, Component.text(Tick.format(tick), Colors.AQUA), 0, 10, 0);
        
        // Birds fx
        final Location location = entity.getEyeLocation().add(0, 0.5, 0);
        final double theta = Math.toRadians(tick * 10);
        
        final double spread = Math.PI * 2 / 4;
        
        for (int i = 0; i < 4; ++i) {
            final double thetaOffset = theta + spread * i;
            
            final double x = Math.sin(thetaOffset) * 0.5;
            final double y = Math.sin(Math.toRadians(theta) * 10) * 0.1;
            final double z = Math.cos(thetaOffset) * 0.5;
            
            this.spawnBirb(entity, location, x, y, z);
        }
    }
    
    @Override
    public void onRemove(@NotNull HariantEntity entity, @NotNull HariantEntity applier) {
        entity.unsetSitting();
        
        // Fx
        entity.playWorldSound(Sound.ENTITY_HORSE_SADDLE, 0.0f);
    }
    
    @EventHandler
    public void handleHariantDamageEvent(HariantDamageEvent ev) {
        final HariantEntity entity = ev.getEntity();
        
        if (entity.hasEffect(StatusEffectType.STUNNED)) {
            entity.removeEffect(StatusEffectType.STUNNED);
            
            // Multiply damage
            ev.mutateDamage(() -> "Stunned", DamageMutator.multiply(), damageMultiplier);
        }
    }
    
    @EventHandler
    public void handleHariantTalentPreconditionEvent(HariantTalentPreconditionEvent ev) {
        final HariantPlayer player = ev.getPlayer();
        
        if (player.hasEffect(StatusEffectType.STUNNED)) {
            ev.setCancel(Cancel.cancel(Component.text("Cannot use talents while stunned!")));
        }
    }
    
    @EventHandler
    public void handleHariantAttackEvent(HariantAttackEvent ev) {
        final HariantEntity attacker = ev.getAttacker();
        
        if (attacker.hasEffect(StatusEffectType.STUNNED)) {
            ev.setCancelled(true);
            attacker.sendMessage(Component.text("Cannot attack while stunned!"));
        }
    }
    
    private void spawnBirb(@NotNull HariantEntity entity, @NotNull Location location, double x, double y, double z) {
        location.add(x, y, z);
        entity.spawnWorldParticle(location, Particle.CRIT, 1, 0.0f);
        location.subtract(x, y, z);
    }
    
}