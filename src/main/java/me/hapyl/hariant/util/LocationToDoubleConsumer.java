package me.hapyl.hariant.util;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface LocationToDoubleConsumer {
    
    void apply(@NotNull Location location, final double value);
    
}
