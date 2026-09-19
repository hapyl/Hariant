package me.hapyl.hariant.ui;

import me.hapyl.hariant.util.MatrixUtils;
import org.bukkit.Location;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

public interface ComponentDisplayAnimation {
    
    void animate(@NotNull TextDisplay textDisplay, final ComponentOrigin origin, final int currentTick, final int maxTick);
    
    static @NotNull ComponentDisplayAnimation ofFalloff() {
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
            textDisplay.setTransformation(MatrixUtils.scale(newScale));
            
            final Location location = textDisplay.getLocation();
            location.setY(y);
            
            textDisplay.teleport(location);
        };
    }
    
    static @NotNull ComponentDisplayAnimation ofSineAscend() {
        return ofSine0(true);
    }
    
    static @NotNull ComponentDisplayAnimation ofSineDescend() {
        return ofSine0(false);
    }
    
    private static @NotNull ComponentDisplayAnimation ofSine0(boolean ascend) {
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
