package me.hapyl.hariant.entity.effect;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.hariant.entity.HariantEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface Effect extends Keyed, Named {
    
    @NotNull Key getKey();
    
    @Override
    @NotNull Component getName();
    
    @NotNull EffectType getEffectType();
    
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
    default void onApply(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int duration) {
    }
    
    @EventLike
    default void onRemove(@NotNull HariantEntity entity, @NotNull HariantEntity applier) {
    }
    
    @EventLike
    default void onTick(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int tick) {
    }
    
    static @NotNull Effect create(@NotNull Key key, @NotNull Component component, @NotNull EffectType effectType) {
        return new EffectImpl(key, component, effectType);
    }
    
    static @NotNull <K extends Keyed & Named> Effect create(@NotNull K k, @NotNull EffectType effectType) {
        return new EffectImpl(k.getKey(), k.getName(), effectType);
    }
    
}