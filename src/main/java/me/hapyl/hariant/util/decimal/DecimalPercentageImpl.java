package me.hapyl.hariant.util.decimal;

public class DecimalPercentageImpl extends DecimalImpl {
    
    private static final int DIVISOR = 100;
    
    DecimalPercentageImpl(double value) {
        super(value, DecimalFormat.decimal("%,.0f%%", "%,.2f%%"));
    }
    
    @Override
    public int intValue() {
        return (int) (this.value / DIVISOR);
    }
    
    @Override
    public long longValue() {
        return (long) (this.value / DIVISOR);
    }
    
    @Override
    public float floatValue() {
        return (float) (this.value / DIVISOR);
    }
    
    @Override
    public double doubleValue() {
        return this.value / DIVISOR;
    }
}
