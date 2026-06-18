package me.hapyl.hariant.util;

import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public interface QuaternionRotation {
    
    @NotNull Quaternionf rotate(@NotNull Quaternionf quaternion, float angle);
    
}
