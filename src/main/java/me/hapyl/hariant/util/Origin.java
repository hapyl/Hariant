package me.hapyl.hariant.util;

import me.hapyl.eterna.module.annotate.DefensiveCopy;
import me.hapyl.eterna.module.annotate.Mutates;
import me.hapyl.eterna.module.location.Coordinates;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.location.Rotation;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class Origin implements Coordinates, Rotation {
    
    private final double x;
    private final double y;
    private final double z;
    
    private final float yaw;
    private final float pitch;
    
    Origin(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }
    
    @Override
    public double x() {
        return x;
    }
    
    @Override
    public double y() {
        return y;
    }
    
    @Override
    public double z() {
        return z;
    }
    
    @Override
    public float yaw() {
        return yaw;
    }
    
    @Override
    public float pitch() {
        return pitch;
    }
    
    public @NotNull Location merge(@NotNull @Mutates Location location, double x, double y, double z) {
        return location.set(this.x + x, this.y + y, this.z + z);
    }
    
    public @NotNull Location mergeCopy(@NotNull @DefensiveCopy Location location, double x, double y, double z) {
        return this.merge(LocationHelper.copyOf(location), x, y, z);
    }
    
    public static @NotNull Origin create(@NotNull Location location) {
        return new Origin(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
    
}