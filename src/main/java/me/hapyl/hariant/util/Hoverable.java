package me.hapyl.hariant.util;

import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

public interface Hoverable {
    
    @NotNull
    HoverEvent<?> createHoverEvent();
    
}
