package me.hapyl.hariant.util;

import org.jetbrains.annotations.NotNull;

public interface Arithmetic<T> {
    
    double add(@NotNull T that);
    
    double subtract(@NotNull T that);
    
    double multiply(@NotNull T that);
    
    double divide(@NotNull T that);
    
}
