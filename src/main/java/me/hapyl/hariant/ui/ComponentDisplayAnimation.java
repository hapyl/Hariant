package me.hapyl.hariant.ui;

import org.bukkit.Location;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public interface ComponentDisplayAnimation {
    
    void animate(@NotNull TextDisplay textDisplay, final ComponentOrigin origin, final int currentTick, final int maxTick);
    
    @NotNull
    static ComponentDisplayAnimation ofFalloff() {
        class Holder {
            private static final double RAD_200 = Math.toRadians(200);
        }
        
        return (textDisplay, origin, tick, duration) -> {
            // Calculate Y
            final double progress = (double) tick / duration;
            final double radians = Holder.RAD_200 * progress;
            final double y = origin.y() + Math.sin(radians) * 0.5;
            
            final byte opacity = (byte) (-100 * progress);
            final float newScale = (float) (origin.scale() * (1 - progress));
            
            textDisplay.setTextOpacity(opacity);
            textDisplay.setTransformation(new Transformation(
                    new Vector3f(0, 0, 0),
                    new AxisAngle4f(0, 0, 0, 0),
                    new Vector3f(newScale, newScale, newScale),
                    new AxisAngle4f(0, 0, 0, 0)
            ));
            
            final Location location = textDisplay.getLocation();
            location.setY(y);
            
            textDisplay.teleport(location);
        };
    }
    
    @NotNull
    static ComponentDisplayAnimation ofSineAscend() {
        return ofSine0(true);
    }
    
    @NotNull
    static ComponentDisplayAnimation ofSineDescend() {
        return ofSine0(false);
    }
    
    @NotNull
    private static ComponentDisplayAnimation ofSine0(boolean ascend) {
        return (textDisplay, origin, currentTick, maxTick) -> {
            final Location location = textDisplay.getLocation();
            
            final double progress = (double) currentTick / maxTick;
            final double sine = Math.PI * Math.sin(progress) * 0.5;
            final double y = ascend ? origin.y() + sine : origin.y() - sine;
            
            final byte opacity = (byte) (-250 * progress);
            
            textDisplay.setTextOpacity(opacity);
            
            location.setY(y);
            textDisplay.teleport(location);
        };
    }
    
}
