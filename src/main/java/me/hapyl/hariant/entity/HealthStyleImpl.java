package me.hapyl.hariant.entity;

import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

public class HealthStyleImpl implements HealthStyle {
    
    private final Style healthStyle;
    private final Style heartStyle;
    
    HealthStyleImpl(@NotNull Style healthStyle, @NotNull Style heartStyle) {
        this.healthStyle = healthStyle;
        this.heartStyle = heartStyle;
    }
    
    @Override
    public @NotNull Style getHealthStyle() {
        return healthStyle;
    }
    
    @Override
    public @NotNull Style getHeartStyle() {
        return heartStyle;
    }
    
}
