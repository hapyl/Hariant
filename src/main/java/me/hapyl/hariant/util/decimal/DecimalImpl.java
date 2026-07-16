package me.hapyl.hariant.util.decimal;

import me.hapyl.hariant.Colors;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class DecimalImpl extends Decimal {
    
    private final DecimalFormat format;
    
    DecimalImpl(double value, @NotNull DecimalFormat format) {
        super(value);
        
        this.format = format;
    }
    
    @NotNull
    @Override
    public Component format() {
        return format.format(this.value()).color(Colors.NUMBER);
    }
    
    
}