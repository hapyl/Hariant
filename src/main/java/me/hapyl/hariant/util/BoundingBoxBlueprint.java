package me.hapyl.hariant.util;

import me.hapyl.eterna.module.location.LocationHelper;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

public class BoundingBoxBlueprint implements ComponentFormatter {
    
    private final double[] region;
    private final Component component;
    
    private BoundingBoxBlueprint(double[] region) {
        this.region = region;
        this.component = Component.text("%.1f x %.1f x %.1f".formatted(region[0], region[1], region[2]));
    }
    
    public @NotNull BoundingBox create(@NotNull Location location) {
        return LocationHelper.toBoundingBox(location, region[0], region[1], region[2]);
    }
    
    @Override
    public @NotNull Component format() {
        return component;
    }
    
    public static @NotNull BoundingBoxBlueprint define(double x, double y, double z) {
        return new BoundingBoxBlueprint(new double[] { x, y, z });
    }
}