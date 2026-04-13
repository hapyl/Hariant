package me.hapyl.hariant.math;

import me.hapyl.eterna.module.math.geometry.Drawable;
import me.hapyl.hariant.HariantConstants;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public final class ShapeIcosahedron implements Shape {
    
    private static final Vector[] VERTICES = {
            new Vector(-1, HariantConstants.PHI, 0),
            new Vector(1, HariantConstants.PHI, 0),
            new Vector(-1, -HariantConstants.PHI, 0),
            new Vector(1, -HariantConstants.PHI, 0),
            
            new Vector(0, -1, HariantConstants.PHI),
            new Vector(0, 1, HariantConstants.PHI),
            new Vector(0, -1, -HariantConstants.PHI),
            new Vector(0, 1, -HariantConstants.PHI),
            
            new Vector(HariantConstants.PHI, 0, -1),
            new Vector(HariantConstants.PHI, 0, 1),
            new Vector(-HariantConstants.PHI, 0, -1),
            new Vector(-HariantConstants.PHI, 0, 1)
    };
    
    private static final int[][] EDGES = {
            { 0, 1 }, { 0, 5 }, { 0, 7 }, { 0, 10 }, { 0, 11 },
            { 1, 5 }, { 1, 7 }, { 1, 8 }, { 1, 9 },
            { 2, 3 }, { 2, 4 }, { 2, 6 }, { 2, 10 }, { 2, 11 },
            { 3, 4 }, { 3, 6 }, { 3, 8 }, { 3, 9 },
            { 4, 5 }, { 4, 9 }, { 4, 11 },
            { 5, 9 }, { 5, 11 },
            { 6, 7 }, { 6, 8 }, { 6, 10 },
            { 7, 8 }, { 7, 10 },
            { 8, 9 },
            { 10, 11 }
    };
    
    @Override
    public void draw(@NotNull Location location, @NotNull Drawable drawable, @NotNull ShapeProperties properties) {
        final Vector[] verticesScaled = new Vector[VERTICES.length];
        
        for (int i = 0; i < VERTICES.length; i++) {
            final Vector vector = VERTICES[i].clone().normalize().multiply(properties.scale());
            
            verticesScaled[i] = properties.rotate(vector);
        }
        
        for (int[] edge : EDGES) {
            ShapeOctahedron.drawEdge(location, verticesScaled[edge[0]], verticesScaled[edge[1]], properties.step(), drawable);
        }
    }
    
    
}
