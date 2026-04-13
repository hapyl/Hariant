package me.hapyl.hariant.util;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.hariant.HariantConstants;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface TickDuration {
    
    int currentTick();
    
    int duration();
    
    default boolean isOver() {
        return this.duration() != HariantConstants.INDEFINITE_DURATION && this.currentTick() <= 0;
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
