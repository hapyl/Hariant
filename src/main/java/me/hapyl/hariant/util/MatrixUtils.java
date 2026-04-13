package me.hapyl.hariant.util;

import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public final class MatrixUtils {
    
    private MatrixUtils() {
    }
    
    @NotNull
    public static Transformation scale(final float scale) {
        return new Transformation(
                new Vector3f(0, 0, 0),
                new AxisAngle4f(0, 0, 0, 0),
                new Vector3f(scale, scale, scale),
                new AxisAngle4f(0, 0, 0, 0)
        );
    }
    
}
