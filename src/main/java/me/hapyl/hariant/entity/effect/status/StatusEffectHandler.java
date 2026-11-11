package me.hapyl.hariant.entity.effect.status;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

public interface StatusEffectHandler {
    
    void addEffect(@NotNull StatusEffectType effectType, int duration, @NotNull HariantEntity applier);
    
    default void addEffect(@NotNull StatusEffectType effectType, @NotNull Tick duration, @NotNull HariantEntity applier) {
        this.addEffect(effectType, duration.ticks(), applier);
    }
    
    void removeEffect(@NotNull StatusEffectType effect);
    
    int clearEffects();
    
    boolean hasEffect(@NotNull StatusEffectType effect);
    
    @NotNull Optional<StatusEffectInstance> getEffect(@NotNull StatusEffectType effect);
    
    @NotNull Stream<StatusEffectInstance> getEffects();
    
}