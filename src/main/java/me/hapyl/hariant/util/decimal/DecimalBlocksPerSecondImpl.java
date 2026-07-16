package me.hapyl.hariant.util.decimal;

import net.kyori.adventure.text.Component;

public class DecimalBlocksPerSecondImpl extends DecimalImpl {
    
    private static final double MULTIPLIER = 0.05;
    private static final DecimalFormat FORMAT = value -> Component.text("%.0f blocks/second".formatted(value));
    
    DecimalBlocksPerSecondImpl(int blocksPerSecond) {
        super(blocksPerSecond, FORMAT);
    }
    
    @Override
    public int intValue() {
        return (int) (value * MULTIPLIER);
    }
    
    @Override
    public long longValue() {
        return (long) (value * MULTIPLIER);
    }
    
    @Override
    public float floatValue() {
        return (float) (value * MULTIPLIER);
    }
    
    @Override
    public double doubleValue() {
        return value * MULTIPLIER;
    }
    
}
