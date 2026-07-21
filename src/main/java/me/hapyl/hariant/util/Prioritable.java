package me.hapyl.hariant.util;

import org.jetbrains.annotations.NotNull;

public interface Prioritable {
    
    @NotNull Priority getPriority();
    
    default boolean hasHigherPriority(@NotNull Prioritable that) {
        return this.getPriority().isHigher(that.getPriority());
    }
    
}
