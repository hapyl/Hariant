package me.hapyl.hariant.entity.effect;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.HariantEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class EffectImpl implements Effect {
    
    private final Key key;
    private final Component name;
    private final EffectType effectType;
    
    EffectImpl(@NotNull Key key, @NotNull Component name, @NotNull EffectType effectType) {
        this.key = key;
        this.name = name;
        this.effectType = effectType;
    }
    
    @Override
    public @NotNull Key getKey() {
        return key;
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public @NotNull EffectType getEffectType() {
        return effectType;
    }
    
    @Override
    public void onApply(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int duration) {
    }
    
    @Override
    public void onRemove(@NotNull HariantEntity entity, @NotNull HariantEntity applier) {
    }
    
    @Override
    public void onTick(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int tick) {
    }
    
}