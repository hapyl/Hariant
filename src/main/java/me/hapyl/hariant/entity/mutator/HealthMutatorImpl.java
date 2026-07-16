package me.hapyl.hariant.entity.mutator;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

public abstract class HealthMutatorImpl implements HealthMutator {
    
    private final Component name;
    private final Style healthStyle;
    private final Style heartStyle;
    
    HealthMutatorImpl(@NotNull Component name, @NotNull Style healthStyle, @NotNull Style heartStyle) {
        this.name = name;
        this.healthStyle = healthStyle;
        this.heartStyle = heartStyle;
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @NotNull
    @Override
    public Style getHealthStyle() {
        return healthStyle;
    }
    
    @NotNull
    @Override
    public Style getHeartStyle() {
        return heartStyle;
    }
    
    @Override
    public abstract double mutate(double health);
    
}