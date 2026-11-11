package me.hapyl.hariant.entity.cooldown;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import org.jetbrains.annotations.NotNull;

public final class CooldownImpl implements Cooldown {
    
    private final Key key;
    private final Tick cooldown;
    
    CooldownImpl(@NotNull Key key, @NotNull Tick cooldown) {
        this.key = key;
        this.cooldown = cooldown;
    }
    
    @Override
    public @NotNull Key getCooldownKey() {
        return key;
    }
    
    @Override
    public @NotNull Tick getCooldown() {
        return cooldown;
    }
    
}
