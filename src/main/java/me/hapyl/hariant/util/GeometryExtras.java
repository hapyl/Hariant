package me.hapyl.hariant.util;

import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.math.geometry.Drawable;
import me.hapyl.eterna.module.math.geometry.Geometry;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public final class GeometryExtras {
    
    private GeometryExtras() {
    }
    
    public static void drawX(@NotNull Location location, double radius, double step, @NotNull Drawable drawable) {
        drawXPart(location, radius, step, drawable, 1, 1, -1, -1);
        drawXPart(location, radius, step, drawable, -1, -1, 1, 1);
        drawXPart(location, radius, step, drawable, 1, -1, -1, 1);
        drawXPart(location, radius, step, drawable, -1, 1, 1, -1);
    }
    
    private static void drawXPart(@NotNull Location location, double radius, double step, @NotNull Drawable drawable, int x1, int z1, int x2, int z2) {
        Geometry.drawLine(LocationHelper.copyOf(location).add(x1 * radius, radius, z1 * radius), LocationHelper.copyOf(location).add(x2 * radius, -radius, z2 * radius), step, drawable);
    }
    
}
