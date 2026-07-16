package me.hapyl.hariant.util.decimal;

import me.hapyl.hariant.Colors;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class DecimalSecondsImpl extends DecimalImpl {
    
    private static final int MULTIPLIER = 20;
    
    DecimalSecondsImpl(double value) {
        super(value, DecimalFormat.SECONDS);
    }
    
    @Override
    public int intValue() {
        return (int) (this.value * MULTIPLIER);
    }
    
    @Override
    public long longValue() {
        return (long) (this.value * MULTIPLIER);
    }
    
    @Override
    public float floatValue() {
        return (float) (this.value * MULTIPLIER);
    }
    
    @Override
    public double doubleValue() {
        return this.value * MULTIPLIER;
    }
    
    @NotNull
    @Override
    public Component format() {
        return super.format().color(Colors.TICK);
    }
}
