package me.hapyl.hariant.math;

import me.hapyl.eterna.module.math.Vector3;
import org.jetbrains.annotations.NotNull;

public class ShapePropertiesImpl implements ShapeProperties {
    
    private final double scale;
    private final double step;
    private final Vector3 rotation;
    
    ShapePropertiesImpl(double scale, double step, @NotNull Vector3 rotation) {
        this.scale = scale;
        this.step = step;
        this.rotation = rotation;
    }
    
    @Override
    public double scale() {
        return scale;
    }
    
    @Override
    public double step() {
        return step;
    }
    
    @NotNull
    @Override
    public Vector3 rotation() {
        return rotation;
    }
    
}
