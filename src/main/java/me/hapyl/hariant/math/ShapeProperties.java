package me.hapyl.hariant.math;

import me.hapyl.eterna.module.annotate.Mutates;
import me.hapyl.eterna.module.math.Vector3;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public interface ShapeProperties {
    
    double scale();
    
    double step();
    
    @NotNull Vector3 rotation();
    
    default @NotNull Vector rotate(@NotNull @Mutates Vector vector) {
        final Vector3 rotation = rotation();
        
        if (rotation.x() != 0) {
            vector.rotateAroundX(rotation.x());
        }
        
        if (rotation.y() != 0) {
            vector.rotateAroundY(rotation.y());
        }
        
        if (rotation.z() != 0) {
            vector.rotateAroundZ(rotation.z());
        }
        
        return vector;
    }
    
    static @NotNull ShapeProperties create(double scale, double step, @NotNull Vector3 rotation) {
        return new ShapePropertiesImpl(scale, step, rotation);
    }
    
    static @NotNull ShapeProperties create(double scale, double step) {
        return new ShapePropertiesImpl(scale, step, Vector3.zero());
    }
    
}
