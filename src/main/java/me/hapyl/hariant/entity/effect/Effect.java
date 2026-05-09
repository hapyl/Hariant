package me.hapyl.hariant.entity.effect;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;

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
    void onApply(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int duration);
    
    @EventLike
    void onRemove(@NotNull HariantEntity entity, @NotNull HariantEntity applier);
    
    @EventLike
    void onTick(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int tick);
}
