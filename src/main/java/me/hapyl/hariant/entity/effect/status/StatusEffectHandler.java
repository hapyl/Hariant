package me.hapyl.hariant.entity.effect.status;

import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.util.decimal.Decimal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

public interface StatusEffectHandler {
    
    void addEffect(@NotNull EnumStatusEffect effect, int duration, @Nullable HariantEntity applier);
    
    default void addEffect(@NotNull EnumStatusEffect effect, int duration) {
        this.addEffect(effect, duration, null);
    }
    
    default void addEffect(@NotNull EnumStatusEffect effect, @NotNull Decimal duration, @Nullable HariantEntity applier) {
        this.addEffect(effect, duration.intValue(), applier);
    }
    
    default void addEffect(@NotNull EnumStatusEffect effect, @NotNull Decimal duration) {
        this.addEffect(effect, duration.intValue(), null);
    }
    
    void removeEffect(@NotNull EnumStatusEffect effect);
    
    void resetEffects();
    
    boolean hasEffect(@NotNull EnumStatusEffect effect);
    
    @NotNull
    Optional<StatusEffectInstance> getEffect(@NotNull EnumStatusEffect effect);
    
    @NotNull
    Stream<StatusEffectInstance> getEffects();
}
