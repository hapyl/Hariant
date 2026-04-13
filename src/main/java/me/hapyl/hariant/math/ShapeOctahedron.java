package me.hapyl.hariant.math;

import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.math.geometry.Drawable;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class ShapeOctahedron implements Shape {
    
    private static final Vector[] VERTICES = {
            new Vector(1, 0, 0),
            new Vector(-1, 0, 0),
            new Vector(0, 1, 0),
            new Vector(0, -1, 0),
            new Vector(0, 0, 1),
            new Vector(0, 0, -1)
    };
    
    private static final int[][] EDGES = {
            { 0, 2 }, { 0, 3 }, { 0, 4 }, { 0, 5 },
            { 1, 2 }, { 1, 3 }, { 1, 4 }, { 1, 5 },
            { 2, 4 }, { 2, 5 }, { 3, 4 }, { 3, 5 }
    };
    
    @Override
    public void draw(@NotNull Location location, @NotNull Drawable drawable, @NotNull ShapeProperties properties) {
        final Vector[] verticesScaled = new Vector[VERTICES.length];
        
        for (int i = 0; i < VERTICES.length; i++) {
            final Vector vector = VERTICES[i].clone().normalize().multiply(properties.scale());
            
            verticesScaled[i] = properties.rotate(vector);
        }
        
        for (int[] edge : EDGES) {
            drawEdge(location, verticesScaled[edge[0]], verticesScaled[edge[1]], properties.step(), drawable);
        }
    }
    
    static void drawEdge(@NotNull Location location, @NotNull Vector start, @NotNull Vector end, double step, @NotNull Drawable drawable) {
        final Vector delta = end.clone().subtract(start);
        final double length = delta.length();
        
        final Vector direction = delta.normalize().multiply(step);
        final Location point = LocationHelper.copyOf(location).add(start);
        
        for (double travelled = 0; travelled < length; travelled += step) {
            drawable.draw(point);
            point.add(direction);
        }
    }
    
}
