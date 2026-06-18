package me.hapyl.hariant.util;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.hariant.HariantConstants;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an interface for ticking objects that tick down.
 */
public interface TickDuration {
    
    int currentTick();
    
    int duration();
    
    default boolean isIndefinite() {
        return this.duration() == HariantConstants.INDEFINITE_DURATION;
    }
    
    default boolean isOver() {
        return !this.isIndefinite() && this.currentTick() <= 0;
    }
    
    @NotNull
    default Component currentTickFormatted() {
        return Component.text(Tick.format(this.currentTick()));
    }
    
    @NotNull
    default Component durationFormatted() {
        return Component.text(Tick.format(this.duration()));
    }
    
}
