package me.hapyl.hariant.entity.effect.status;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.element.ElementSource;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.effect.EffectType;
import net.kyori.adventure.text.Component;
import org.bukkit.Particle;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class StatusEffectHellburn extends StatusEffectImpl implements Listener {
    
    StatusEffectHellburn() {
        super(Key.ofString("effect_hellburn"), Component.text("Hellburn"), EffectType.DEBUFF);
    }
    
    @Override
    public void onTick(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int tick, int duration) {
        entity.applyElement(ElementSource.create(ElementType.FIRE, applier, 1));
        
        entity.spawnWorldParticle(entity.getMidpointLocation(), Particle.LAVA, 1, 0.25, 0.25, 0.25, 0.075f);
        entity.spawnWorldParticle(entity.getMidpointLocation(), Particle.FLAME, 1, 0.25, 0.25, 0.25, 0.075f);
    }
    
}