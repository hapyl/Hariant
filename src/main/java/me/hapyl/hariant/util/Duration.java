package me.hapyl.hariant.util;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.hariant.Colors;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface Duration extends ComponentFormatter {
    
    @NotNull Tick getDuration();
    
    default int duration() {
        return this.getDuration().ticks();
    }
    
    void setDuration(@NotNull Tick duration);
    
    @Override
    default @NotNull Component format() {
        return this.asDurationComponent();
    }
    
    @Override
    default @NotNull Component asComponent() {
        return this.asDurationComponentFormatted();
    }
    
    default @NotNull Component asDurationComponentFormatted() {
        return this.asDurationComponent().color(Colors.TICK);
    }
    
    default @NotNull Component asDurationComponent() {
        return this.getDuration().asComponent();
    }
    
}
