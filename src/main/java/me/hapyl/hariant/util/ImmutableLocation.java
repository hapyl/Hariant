package me.hapyl.hariant.util;

import me.hapyl.eterna.module.location.Coordinates;
import me.hapyl.eterna.module.location.Located;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ImmutableLocation implements Coordinates, Located {
    
    private static final World WORLD = Objects.requireNonNull(Bukkit.getWorlds().getFirst(), "Unloaded world!");
    
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    
    ImmutableLocation(final double x, final double y, final double z, final float yaw, final float pitch) {
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
    
    @NotNull
    @Override
    public Location getLocation() {
        return new Location(WORLD, x, y, z, yaw, pitch);
    }
    
    @NotNull
    @Override
    public World getWorld() {
        return WORLD;
    }
    
    @NotNull
    public static ImmutableLocation create(final double x, final double y, final double z, final float yaw, final float pitch) {
        return new ImmutableLocation(x, y, z, yaw, pitch);
    }
    
    @NotNull
    public static ImmutableLocation create(final double x, final double y, final double z) {
        return create(x, y, z, 0f, 0f);
    }
    
    @NotNull
    public static ImmutableLocation create(@NotNull Location location) {
        return create(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
    
}
