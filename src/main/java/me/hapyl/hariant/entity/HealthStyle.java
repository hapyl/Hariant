package me.hapyl.hariant.entity;

import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

public interface HealthStyle {
    
    @NotNull Style getHealthStyle();
    
    @NotNull Style getHeartStyle();
    
    static @NotNull HealthStyle create(@NotNull Style healthStyle, @NotNull Style heartStyle) {
        return new HealthStyleImpl(healthStyle, heartStyle);
    }
    
    static @NotNull HealthStyle create(@NotNull Style style) {
        return create(style, style);
    }
    
}
