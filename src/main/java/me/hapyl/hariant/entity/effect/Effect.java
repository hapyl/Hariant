package me.hapyl.hariant.entity.effect;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Effect {
    
    @NotNull
    Key getKey();
    
    @NotNull
    EffectType getEffectType();
    
    default boolean isBuff() {
        return this.getEffectType() == EffectType.BUFF;
    }
    
    default boolean isDebuff() {
        return this.getEffectType() == EffectType.DEBUFF;
    }
    
    default boolean isNeutral() {
        return this.getEffectType() == EffectType.NEUTRAL;
    }
    
    @EventLike
    void onApply(@NotNull HariantEntity entity, @Nullable HariantEntity applier);
    
    @EventLike
    void onRemove(@NotNull HariantEntity entity, @Nullable HariantEntity applier);
    
    @EventLike
    void onTick(@NotNull HariantEntity entity, @Nullable HariantEntity applier, int tick);
}
