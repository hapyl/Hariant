package me.hapyl.hariant.dialog;

public enum DialogSpeed {
    
    NORMAL(1),
    FAST(2),
    VERY_FAST(3);
    
    private final double multiplier;
    
    DialogSpeed(double multiplier) {
        this.multiplier = multiplier;
    }
    
    public double getMultiplier() {
        return multiplier;
    }
}
