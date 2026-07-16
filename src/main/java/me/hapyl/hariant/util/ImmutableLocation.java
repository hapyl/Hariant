package me.hapyl.hariant.util;

import me.hapyl.eterna.module.location.Coordinates;
import me.hapyl.eterna.module.location.Located;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

public final class ImmutableLocation implements Coordinates, Located, Comparable<ImmutableLocation> {
    
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
    public Location getCenteredLocation() {
        return new Location(WORLD, x + 0.5, y, z + 0.5, yaw, pitch);
    }
    
    @Override
    public int compareTo(@NotNull ImmutableLocation that) {
        int value;
        
        if ((value = Double.compare(this.x, that.x)) != 0) {
            return value;
        }
        if ((value = Double.compare(this.y, that.y)) != 0) {
            return value;
        }
        
        return Double.compare(this.z, that.z);
    }
    
    public boolean compare(@NotNull Location location) {
        if (this.x != location.getX()) {
            return false;
        }
        else if (this.y != location.getY()) {
            return false;
        }
        
        return this.z == location.getZ();
    }
    
    public boolean compare(@NotNull Block block) {
        return this.compare(block.getLocation());
    }
    
    public void offset(@NotNull Location location, @NotNull Consumer<Location> consumer) {
        location.add(x, y, z);
        consumer.accept(location);
        location.subtract(x, y, z);
    }
    
    public void offset(@NotNull Location location, @NotNull Runnable runnable) {
        this.offset(location, _location -> runnable.run());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y, this.z);
    }
    
    @Override
    public final boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final ImmutableLocation that = (ImmutableLocation) object;
        return Double.compare(this.x, that.x) == 0 && Double.compare(this.y, that.y) == 0 && Double.compare(this.z, that.z) == 0;
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
