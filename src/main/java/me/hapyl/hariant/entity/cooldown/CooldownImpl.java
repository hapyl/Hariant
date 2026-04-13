package me.hapyl.hariant.entity.cooldown;

import me.hapyl.eterna.module.registry.Key;
import org.jetbrains.annotations.NotNull;

public final class CooldownImpl implements Cooldown {
    
    private final Key key;
    private final int cooldown;
    
    CooldownImpl(@NotNull Key key, final int cooldown) {
        this.key = key;
        this.cooldown = cooldown;
    }
    
    @NotNull
    @Override
    public Key getCooldownKey() {
        return key;
    }
    
    @Override
    public int getCooldown() {
        return cooldown;
    }
    
}
