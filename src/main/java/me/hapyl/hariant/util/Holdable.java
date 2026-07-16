package me.hapyl.hariant.util;

import org.jetbrains.annotations.Nullable;

public interface Holdable<T> {
    
    @Nullable T getHolder();
    
    void setHolder(@Nullable T holder);
    
    default boolean isHeld() {
        return getHolder() != null;
    }
    
}
