package me.hapyl.hariant.entity;

import me.hapyl.eterna.module.location.Located;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.util.decimal.Decimal;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.stream.Stream;

public interface EntityCollector extends Located {
    
    @NotNull
    @Override
    Location getLocation();
    
    @NotNull
    default Color outlineColor() {
        return Color.ORANGE;
    }
    
    @NotNull
    default Stream<HariantEntity> collectNearbyEntities(@NotNull BoundingBox boundingBox) {
        return this.getLocation().getWorld().getNearbyEntities(supplyBoundingBox(this, boundingBox))
                   .stream()
                   .map(Hariant::getEntityOrNull)
                   .filter(Objects::nonNull);
    }
    
    // *-* Primitives *-* //
    
    @NotNull
    default Stream<HariantEntity> collectNearbyEntities(@NotNull Location location, double x, double y, double z) {
        return this.collectNearbyEntities(LocationHelper.toBoundingBox(location, x, y, z));
    }
    
    @NotNull
    default Stream<HariantEntity> collectNearbyEntities(double x, double y, double z) {
        return this.collectNearbyEntities(this.getLocation(), x, y, z);
    }
    
    @NotNull
    default Stream<HariantEntity> collectNearbyEntities(@NotNull Location location, double distance) {
        return this.collectNearbyEntities(location, distance, distance, distance);
    }
    
    @NotNull
    default Stream<HariantEntity> collectNearbyEntities(double distance) {
        return this.collectNearbyEntities(this.getLocation(), distance, distance, distance);
    }
    
    // *-* Decimal *-* //
    
    @NotNull
    default Stream<HariantEntity> collectNearbyEntities(@NotNull Location location, @NotNull Decimal x, @NotNull Decimal y, @NotNull Decimal z) {
        return this.collectNearbyEntities(LocationHelper.toBoundingBox(location, x.doubleValue(), y.doubleValue(), z.doubleValue()));
    }
    
    @NotNull
    default Stream<HariantEntity> collectNearbyEntities(@NotNull Decimal x, @NotNull Decimal y, @NotNull Decimal z) {
        return this.collectNearbyEntities(this.getLocation(), x, y, z);
    }
    
    @NotNull
    default Stream<HariantEntity> collectNearbyEntities(@NotNull Location location, @NotNull Decimal distance) {
        return this.collectNearbyEntities(location, distance, distance, distance);
    }
    
    @NotNull
    default Stream<HariantEntity> collectNearbyEntities(@NotNull Decimal distance) {
        return this.collectNearbyEntities(this.getLocation(), distance, distance, distance);
    }
    
    // *-* Generic Numbers *-* //
    
    @NotNull
    default Stream<HariantEntity> collectNearbyEntities(@NotNull Location location, @NotNull Number x, @NotNull Number y, @NotNull Number z) {
        return this.collectNearbyEntities(location, x.doubleValue(), y.doubleValue(), z.doubleValue());
    }
    
    @NotNull
    default Stream<HariantEntity> collectNearbyEntities(@NotNull Number x, @NotNull Number y, @NotNull Number z) {
        return this.collectNearbyEntities(this.getLocation(), x.doubleValue(), y.doubleValue(), z.doubleValue());
    }
    
    @NotNull
    private static BoundingBox supplyBoundingBox(@NotNull EntityCollector collector, @NotNull BoundingBox boundingBox) {
        // If debug is enabled, draw the outline
        if (BoundingBoxRenderer.DEBUG_DRAW_BOUNDING_BOX_OUTLINES) {
            BoundingBoxRenderer.render(boundingBox, collector.getWorld(), collector.outlineColor());
        }
        
        return boundingBox;
    }
    
    class BoundingBoxRenderer {
        
        public static boolean DEBUG_DRAW_BOUNDING_BOX_OUTLINES = false;
        
        private BoundingBoxRenderer() {
        }
        
        public static void render(@NotNull BoundingBox boundingBox, @NotNull World world, @NotNull Color color) {
            final double minX = boundingBox.getMinX();
            final double minY = boundingBox.getMinY();
            final double minZ = boundingBox.getMinZ();
            final double maxX = boundingBox.getMaxX();
            final double maxY = boundingBox.getMaxY();
            final double maxZ = boundingBox.getMaxZ();
            
            final double step = 0.15;
            
            // Bottom face
            drawLine(minX, minY, minZ, maxX, minY, minZ, step, world, color);
            drawLine(maxX, minY, minZ, maxX, minY, maxZ, step, world, color);
            drawLine(maxX, minY, maxZ, minX, minY, maxZ, step, world, color);
            drawLine(minX, minY, maxZ, minX, minY, minZ, step, world, color);
            
            // Top face
            drawLine(minX, maxY, minZ, maxX, maxY, minZ, step, world, color);
            drawLine(maxX, maxY, minZ, maxX, maxY, maxZ, step, world, color);
            drawLine(maxX, maxY, maxZ, minX, maxY, maxZ, step, world, color);
            drawLine(minX, maxY, maxZ, minX, maxY, minZ, step, world, color);
            
            // Vertical edges
            drawLine(minX, minY, minZ, minX, maxY, minZ, step, world, color);
            drawLine(maxX, minY, minZ, maxX, maxY, minZ, step, world, color);
            drawLine(maxX, minY, maxZ, maxX, maxY, maxZ, step, world, color);
            drawLine(minX, minY, maxZ, minX, maxY, maxZ, step, world, color);
        }
        
        private static void drawLine(double x1, double y1, double z1, double x2, double y2, double z2, double step, @NotNull World world, @NotNull Color color) {
            final double dx = x2 - x1;
            final double dy = y2 - y1;
            final double dz = z2 - z1;
            final double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            
            final int steps = Math.max(1, (int) (distance / step));
            final Particle.DustOptions dustOptions = new Particle.DustOptions(color, 0.5f);
            
            for (int i = 0; i <= steps; i++) {
                double t = (double) i / steps;
                double x = x1 + dx * t;
                double y = y1 + dy * t;
                double z = z1 + dz * t;
                
                final Location location = new Location(world, x, y, z);
                
                world.spawnParticle(Particle.DUST, location, 1, 0, 0, 0, 0f, dustOptions, true);
            }
        }
        
    }
}
