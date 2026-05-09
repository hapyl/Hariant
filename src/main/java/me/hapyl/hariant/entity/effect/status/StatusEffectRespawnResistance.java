package me.hapyl.hariant.entity.effect.status;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.effect.EffectType;
import net.kyori.adventure.text.Component;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

public class StatusEffectRespawnResistance extends StatusEffectImpl {
    StatusEffectRespawnResistance() {
        super(Key.ofString("respawn_resistance"), Component.text("Respawn Resistance"), EffectType.BUFF);
    }
    
    @Override
    public void onApply(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int duration) {
        entity.setInvulnerability(duration + 10);
    }
    
    @Override
    public void onTick(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int tick) {
        entity.spawnWorldParticle(entity.getMidpointLocation(), Particle.ENCHANTED_HIT, 2, 0.2, 0.6, 0.2, 0.15f);
    }
}
