package me.hapyl.hariant.entity.effect.status;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.environment.EnvironmentDamageSource;
import me.hapyl.hariant.entity.effect.EffectType;
import me.hapyl.hariant.event.HariantDamageComputeEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class StatusEffectFallDamageResistance extends StatusEffectImpl implements Listener {
    
    StatusEffectFallDamageResistance() {
        super(Key.ofString("status_effect_fall_damage_resistance"), Component.text("Fall Damage Resistance"), EffectType.BUFF);
        
        setDescription(Component.text("Negates a single instance of fall damage."));
    }
    
    @EventHandler
    public void handleHariantDamageComputeEvent(HariantDamageComputeEvent ev) {
        final HariantEntity entity = ev.getEntity();
        
        if (!(ev.getDamageSource() instanceof EnvironmentDamageSource environmentDamageSource)) {
            return;
        }
        
        if (!environmentDamageSource.isFall()) {
            return;
        }
        
        if (!entity.hasEffect(StatusEffectType.FALL_DAMAGE_RESISTANCE)) {
            return;
        }
        
        entity.removeEffect(StatusEffectType.FALL_DAMAGE_RESISTANCE);
        ev.cancel(HariantDamageComputeEvent.cancel(this));
    }
    
}
