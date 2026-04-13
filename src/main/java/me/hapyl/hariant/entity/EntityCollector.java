package me.hapyl.hariant.entity;

import me.hapyl.eterna.module.location.Located;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.util.decimal.Decimal;
import org.bukkit.Location;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface EntityCollector extends Located {
    
    @NotNull
    @Override
    Location getLocation();
    
    // *-* double *-* //
    
    @NotNull
    default Stream<HariantEntity> collectionNearbyEntities(@NotNull BoundingBox boundingBox) {
        return this.getLocation().getWorld().getNearbyEntities(boundingBox)
                   .stream()
                   .map(Hariant::getEntityOrNull)
                   .filter(Objects::nonNull);
    }
    
    @NotNull
    default Stream<HariantEntity> collectNearbyEntities(@NotNull Location location, double distance) {
        return location.getWorld().getNearbyEntities(location, distance, distance, distance)
                       .stream()
                       .map(Hariant::getEntityOrNull)
                       .filter(distanceFilter(location, distance));
    }
    
    @NotNull
    default Stream<HariantEntity> collectNearbyEntities(double distance) {
        return this.collectNearbyEntities(this.getLocation(), distance);
    }
    
    // *-* Decimal *-* //
    
    @NotNull
    default Stream<HariantEntity> collectNearbyEntities(@NotNull Location location, @NotNull Decimal distance) {
        return this.collectNearbyEntities(location, distance.doubleValue());
    }
    
    @NotNull
    default Stream<HariantEntity> collectNearbyEntities(@NotNull Decimal distance) {
        return this.collectNearbyEntities(this.getLocation(), distance.doubleValue());
    }
    
    @NotNull
    private static Predicate<HariantEntity> distanceFilter(@NotNull Location location, double distance) {
        final double distanceSquared = distance * distance;
        
        return entity -> {
            if (entity == null) {
                return false;
            }
            
            final BoundingBox boundingBox = entity.getHandle().getBoundingBox();
            
            final double x = Math.clamp(location.x(), boundingBox.getMinX(), boundingBox.getMaxX());
            final double y = Math.clamp(location.y(), boundingBox.getMinY(), boundingBox.getMaxY());
            final double z = Math.clamp(location.z(), boundingBox.getMinZ(), boundingBox.getMaxZ());
            
            final double dx = location.x() - x;
            final double dy = location.y() - y;
            final double dz = location.z() - z;
            
            return dx * dx + dy * dy + dz * dz <= distanceSquared;
        };
    }
    
}
