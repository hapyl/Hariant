package me.hapyl.hariant.dialog;

import me.hapyl.eterna.module.text.Capitalizable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public enum DialogSpeed implements ComponentLike {
    
    NORMAL(1),
    FAST(2),
    VERY_FAST(3);
    
    private final double multiplier;
    private final Component component;
    
    DialogSpeed(double multiplier) {
        this.multiplier = multiplier;
        this.component = Component.text("%s (%.0fx)".formatted(Capitalizable.capitalize(this), multiplier));
    }
    
    public double getMultiplier() {
        return multiplier;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return component;
    }
}
