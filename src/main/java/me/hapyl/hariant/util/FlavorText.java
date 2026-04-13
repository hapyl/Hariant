package me.hapyl.hariant.util;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface FlavorText {
    
    @NotNull
    Component getFlavorText();
    
    default void setFlavorText(@NotNull Component flavorText) {
        throw new UnsupportedOperationException("flavorText");
    }
    
}
