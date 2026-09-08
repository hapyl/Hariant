package me.hapyl.hariant.util;

import org.jetbrains.annotations.NotNull;

public interface Contains<T> {
    
    boolean contains(@NotNull T t);
    
}
