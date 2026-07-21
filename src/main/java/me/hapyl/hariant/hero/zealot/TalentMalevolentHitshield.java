package me.hapyl.hariant.hero.zealot;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.entity.shield.Shield;
import me.hapyl.hariant.entity.shield.ShieldStrength;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.Priority;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public final class TalentMalevolentHitshield extends Talent {
    
    private final @DisplayField Decimal shieldCapacity = Decimal.ofValue(10);
    
    public TalentMalevolentHitshield(@NotNull Key key) {
        super(key, Component.text("Malevolent Hitshield"), Icon.ofMaterial(Material.ENDER_EYE));
        
        setTalentType(TalentType.DEFENSE);
        
        setCooldownSeconds(16);
        
        setDescription(
                Component.empty()
                         .append(Component.text("Activate a hitshield that blocks any type and amount of "))
                         .append(Component.text("DMG", Colors.RED))
                         .append(Component.text(" for a maximum of "))
                         .append(shieldCapacity)
                         .append(Component.text(" times."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Cooldown of this talent starts when the shield breaks.", Colors.DARK_GRAY))
        );
    }
    
    @Override
    public @NotNull TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @Override
    public @NotNull Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        player.setShield(new MalevolentHitshield(player));
        
        return Response.await();
    }
    
    private class MalevolentHitshield extends Shield {
        
        MalevolentHitshield(@NotNull HariantEntity entity) {
            super(entity, entity, ShieldStrength.always1(), shieldCapacity.intValue(), HariantConstants.INDEFINITE_DURATION);
        }
        
        @Override
        public @NotNull Priority getPriority() {
            return Priority.VERY_HIGH;
        }
        
        @Override
        public double shield(double damage) {
            return --this.capacity;
        }
        
        @Override
        public boolean canShield(@NotNull DamageSource damageSource) {
            // Always shields, regardless of the source
            return true;
        }
        
        @Override
        public void onCreate() {
            entity.playWorldSound(Sound.ENTITY_ENDER_DRAGON_HURT, 0.0f);
        }
        
        @Override
        public void onHit(double amount) {
            // Fx
            final Location location = entity.getLocation();
            
            entity.playWorldSound(Sound.ENTITY_ENDERMAN_TELEPORT, (float) (2.0f - (1.5f * capacity / getMaximumCapacity())));
            
            entity.spawnWorldParticle(location, Particle.PORTAL, 10, 0, 0, 0, 1.0f);
            entity.spawnWorldParticle(location, Particle.REVERSE_PORTAL, 10, 0, 0, 0, 1.0f);
        }
        
        @Override
        public void onRemove(@NotNull Cause cause) {
            if (cause != Cause.BROKE) {
                return;
            }
            
            // Start the cooldown
            entity.setCooldown(TalentMalevolentHitshield.this);
            
            // Fx
            entity.playWorldSound(Sound.ENTITY_ENDERMAN_HURT, 0.0f);
            entity.playWorldSound(Sound.ENTITY_ENDERMAN_DEATH, 1.25f);
            
            entity.spawnWorldParticle(entity.getMidpointLocation(), Particle.WITCH, 50, 0.2, 0.5, 0.2, 0.75f);
        }
    }
    
}