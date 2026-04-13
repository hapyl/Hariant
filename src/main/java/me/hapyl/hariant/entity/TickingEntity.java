package me.hapyl.hariant.entity;

import org.jetbrains.annotations.NotNull;

public interface TickingEntity {
    
    void tick(@NotNull HariantEntity entity);
    
}
