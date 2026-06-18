package me.hapyl.hariant.entity.mutator;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.hariant.entity.HariantEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

public interface HealthMutator extends Named {
    
    @NotNull
    @Override
    Component getName();
    
    @NotNull
    Style getHealthStyle();
    
    @NotNull
    Style getHeartStyle();
    
    double mutate(double health);
    
    void tick(@NotNull HariantEntity entity);
    
    boolean isOver();
    
    void onApply(@NotNull HariantEntity entity);
    
    void onRemove(@NotNull HariantEntity entity);
    
}