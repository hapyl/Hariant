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
    
    @Override
    @NotNull Location getLocation();
    
    default @NotNull Color outlineColor() {
        return Color.ORANGE;
    }
    
    default @NotNull Stream<? extends HariantEntity> collectNearbyEntities(@NotNull BoundingBox boundingBox) {
        return streamEntities(this.getWorld(), supplyBoundingBox(this, boundingBox));
    }
    
    default @NotNull Stream<? extends HariantEntity> collectNearbyEntities(@NotNull Location location, double x, double y, double z) {
        return this.collectNearbyEntities(LocationHelper.toBoundingBox(location, x, y, z));
    }
    
    // *-* Primitives *-* //
    
    default @NotNull Stream<? extends HariantEntity> collectNearbyEntities(double x, double y, double z) {
        return this.collectNearbyEntities(this.getLocation(), x, y, z);
    }
    
    default @NotNull Stream<? extends HariantEntity> collectNearbyEntities(@NotNull Location location, double distance) {
        return this.collectNearbyEntities(location, distance, distance, distance);
    }
    
    default @NotNull Stream<? extends HariantEntity> collectNearbyEntities(double distance) {
        return this.collectNearbyEntities(this.getLocation(), distance, distance, distance);
    }
    
    default @NotNull Stream<? extends HariantEntity> collectNearbyEntities(@NotNull Location location, @NotNull Decimal x, @NotNull Decimal y, @NotNull Decimal z) {
        return this.collectNearbyEntities(LocationHelper.toBoundingBox(location, x.doubleValue(), y.doubleValue(), z.doubleValue()));
    }
    
    // *-* Decimal *-* //
    
    default @NotNull Stream<? extends HariantEntity> collectNearbyEntities(@NotNull Decimal x, @NotNull Decimal y, @NotNull Decimal z) {
        return this.collectNearbyEntities(this.getLocation(), x, y, z);
    }
    
    default @NotNull Stream<? extends HariantEntity> collectNearbyEntities(@NotNull Location location, @NotNull Decimal distance) {
        return this.collectNearbyEntities(location, distance, distance, distance);
    }
    
    default @NotNull Stream<? extends HariantEntity> collectNearbyEntities(@NotNull Decimal distance) {
        return this.collectNearbyEntities(this.getLocation(), distance, distance, distance);
    }
    
    default @NotNull Stream<? extends HariantEntity> collectNearbyEntities(@NotNull Location location, @NotNull Number x, @NotNull Number y, @NotNull Number z) {
        return this.collectNearbyEntities(location, x.doubleValue(), y.doubleValue(), z.doubleValue());
    }
    
    static @NotNull Stream<? extends HariantEntity> streamEntities(@NotNull World world, @NotNull BoundingBox boundingBox) {
        return world.getNearbyEntities(boundingBox)
                    .stream()
                    .map(Hariant::getEntityOrNull)
                    .filter(Objects::nonNull);
    }
    
    private static @NotNull BoundingBox supplyBoundingBox(@NotNull EntityCollector collector, @NotNull BoundingBox boundingBox) {
        // If debug is enabled, draw the outline
        if (BoundingBoxRenderer.DEBUG_DRAW_BOUNDING_BOX_OUTLINES) {
            BoundingBoxRenderer.render(boundingBox, collector.getWorld(), collector.outlineColor(), 0.5f);
        }
        
        return boundingBox;
    }
    
    class BoundingBoxRenderer {
        
        public static boolean DEBUG_DRAW_BOUNDING_BOX_OUTLINES = false;
        
        private BoundingBoxRenderer() {
        }
        
        public static void render(@NotNull BoundingBox boundingBox, @NotNull World world, @NotNull Color color, float scale) {
            final double minX = boundingBox.getMinX();
            final double minY = boundingBox.getMinY();
            final double minZ = boundingBox.getMinZ();
            final double maxX = boundingBox.getMaxX();
            final double maxY = boundingBox.getMaxY();
            final double maxZ = boundingBox.getMaxZ();
            
            final Particle.DustOptions dustOptions = new Particle.DustOptions(color, scale);
            
            // Bottom face
            drawLine(minX, minY, minZ, maxX, minY, minZ, world, dustOptions);
            drawLine(maxX, minY, minZ, maxX, minY, maxZ, world, dustOptions);
            drawLine(maxX, minY, maxZ, minX, minY, maxZ, world, dustOptions);
            drawLine(minX, minY, maxZ, minX, minY, minZ, world, dustOptions);
            
            // Top face
            drawLine(minX, maxY, minZ, maxX, maxY, minZ, world, dustOptions);
            drawLine(maxX, maxY, minZ, maxX, maxY, maxZ, world, dustOptions);
            drawLine(maxX, maxY, maxZ, minX, maxY, maxZ, world, dustOptions);
            drawLine(minX, maxY, maxZ, minX, maxY, minZ, world, dustOptions);
            
            // Vertical edges
            drawLine(minX, minY, minZ, minX, maxY, minZ, world, dustOptions);
            drawLine(maxX, minY, minZ, maxX, maxY, minZ, world, dustOptions);
            drawLine(maxX, minY, maxZ, maxX, maxY, maxZ, world, dustOptions);
            drawLine(minX, minY, maxZ, minX, maxY, maxZ, world, dustOptions);
        }
        
        private static void drawLine(double x1, double y1, double z1, double x2, double y2, double z2, @NotNull World world, @NotNull Particle.DustOptions dustOptions) {
            final double dx = x2 - x1;
            final double dy = y2 - y1;
            final double dz = z2 - z1;
            final double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            
            final int steps = Math.max(1, (int) (distance / 0.15));
            
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
