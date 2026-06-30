package me.hapyl.hariant.entity;

import org.jetbrains.annotations.NotNull;

public interface EntitySpawner<H extends HariantEntity> {
    
    @NotNull
    H spawn();
    
}
