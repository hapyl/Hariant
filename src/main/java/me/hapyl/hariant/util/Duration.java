package me.hapyl.hariant.util;

import me.hapyl.hariant.Colors;
import me.hapyl.hariant.util.decimal.DecimalFormat;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface Duration {
    
    int getDuration();
    
    default float getDurationSeconds() {
        return this.getDuration() / 20f;
    }
    
    void setDuration(int duration);
    
    default void setDurationSeconds(float durationSeconds) {
        this.setDuration((int) (durationSeconds * 20));
    }
    
    @NotNull
    default Component getDurationFormatted() {
        return DecimalFormat.SECONDS.format(this.getDurationSeconds()).color(Colors.FORMAT_TICK);
    }
    
}
