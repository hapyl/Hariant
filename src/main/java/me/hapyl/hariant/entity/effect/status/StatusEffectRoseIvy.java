package me.hapyl.hariant.entity.effect.status;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageFlag;
import me.hapyl.hariant.entity.damage.DamageResult;
import me.hapyl.hariant.entity.damage.DamageSourceImpl;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.effect.EffectType;
import me.hapyl.hariant.event.HariantEntityMoveEvent;
import me.hapyl.hariant.talent.TalentRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public final class StatusEffectRoseIvy extends StatusEffectImpl implements Listener {
    
    StatusEffectRoseIvy() {
        super(Key.ofString("effect_rose_ivy"), Component.text("Rose Ivy"), EffectType.DEBUFF);
    }
    
    @EventHandler
    public void handleHariantEntityMoveEvent(HariantEntityMoveEvent ev) {
        final HariantEntity entity = ev.getEntity();
        final StatusEffectInstance effectInstance = entity.getEffect(EnumStatusEffect.ROSE_IVY).orElse(null);
        
        if (effectInstance == null || !ev.hasChangedBlock()) {
            return;
        }
        
        final HariantEntity applier = effectInstance.getApplier();
        
        if (applier == null) {
            return;
        }
        
        if (entity.damage(new RoseIvyDamageSource(applier, TalentRegistry.ROSE_IVY.damage.getScaledValue(applier))) == DamageResult.OK) {
            // Fx
            entity.playWorldSound(Sound.ENCHANT_THORNS_HIT, 0.75f);
        }
    }
    
    public class RoseIvyDamageSource extends DamageSourceImpl {
        RoseIvyDamageSource(@Nullable HariantEntity attacker, double damage) {
            super(
                    StatusEffectRoseIvy.this,
                    attacker,
                    DamageType.TALENT,
                    ElementType.PHYSICAL,
                    List.of(),
                    Set.of(DamageFlag.CANNOT_KILL),
                    damage,
                    TalentRegistry.ROSE_IVY.elementalApplication.doubleValue()
            );
        }
        
        @NotNull
        @Override
        public Key getCooldownKey() {
            return StatusEffectRoseIvy.this.getKey();
        }
        
        @Override
        public int getCooldown() {
            return 5;
        }
    }
    
}