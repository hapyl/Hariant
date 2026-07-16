package me.hapyl.hariant.util;

import org.jetbrains.annotations.NotNull;

public interface Flippable<E> {
    
    @NotNull E flipValue();
    
}
