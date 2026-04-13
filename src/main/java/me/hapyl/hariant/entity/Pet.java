package me.hapyl.hariant.entity;

import org.jetbrains.annotations.NotNull;

public interface Pet {
    
    @NotNull
    HariantEntity owner();
    
}
