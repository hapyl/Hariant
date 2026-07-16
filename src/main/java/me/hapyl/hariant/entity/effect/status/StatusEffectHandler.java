package me.hapyl.hariant.entity.effect.status;

import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.util.decimal.Decimal;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

public interface StatusEffectHandler {
    
    void addEffect(@NotNull EnumStatusEffect effect, int duration, @NotNull HariantEntity applier);
    
    default void addEffect(@NotNull EnumStatusEffect effect, @NotNull Decimal duration, @NotNull HariantEntity applier) {
        this.addEffect(effect, duration.intValue(), applier);
    }
    
    void removeEffect(@NotNull EnumStatusEffect effect);
    
    void resetEffects();
    
    boolean hasEffect(@NotNull EnumStatusEffect effect);
    
    @NotNull
    Optional<StatusEffectInstance> getEffect(@NotNull EnumStatusEffect effect);
    
    @NotNull
    Stream<StatusEffectInstance> getEffects();
    
}