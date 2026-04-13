package me.hapyl.hariant.math;

import me.hapyl.eterna.module.math.geometry.Drawable;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface Shape {
    
    void draw(@NotNull Location location, @NotNull Drawable drawable, @NotNull ShapeProperties properties);
    
}
