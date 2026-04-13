package me.hapyl.hariant.util.decimal;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface DecimalFormat {
    
    @NotNull
    DecimalFormat FLAT = value -> Component.text("%,.0f".formatted(value));
    
    @NotNull
    DecimalFormat PERCENTAGE = value -> Component.text("%,.0f%%".formatted(value));
    
    @NotNull
    DecimalFormat DECIMAL = decimal("%,.0f", "%,.1f");
    
    @NotNull
    DecimalFormat SECONDS = decimal("%,.0fs", "%,.1fs");
    
    @NotNull
    Component format(double value);
    
    @NotNull
    static DecimalFormat decimal(@NotNull String formatInteger, @NotNull String formatDecimal) {
        return value -> Component.text(value % 1 == 0 ? formatInteger.formatted(value) : formatDecimal.formatted(value));
    }
    
}
