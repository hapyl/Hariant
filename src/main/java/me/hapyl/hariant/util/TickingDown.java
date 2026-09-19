package me.hapyl.hariant.util;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.hariant.HariantConstants;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an interface for ticking objects that tick down.
 */
public interface TickingDown {
    
    int currentTick();
    
    int duration();
    
    default boolean isIndefinite() {
        return this.duration() == HariantConstants.INDEFINITE_DURATION;
    }
    
    default boolean isOver() {
        return !this.isIndefinite() && this.currentTick() <= 0;
    }
    
    default @NotNull Component currentTickFormatted() {
        return this.isIndefinite()
               ? HariantConstants.CHARACTER_INFINITY
               : Component.text(Tick.format(this.currentTick()));
    }
    
    default @NotNull Component durationFormatted() {
        return Component.text(Tick.format(this.duration()));
    }
    
}
