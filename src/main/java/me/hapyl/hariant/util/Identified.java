package me.hapyl.hariant.util;

import org.jetbrains.annotations.NotNull;

public interface Identified {
    
    @NotNull
    String identify();
    
    static @NotNull String ofClassName(@NotNull Object object) {
        return object.getClass().getSimpleName().replaceAll("(?<!^)(?=[A-Z])", " ");
    }
}
