package me.hapyl.hariant.entity.damage;

public class KnockbackSourceImpl implements KnockbackSource {
    
    private final double x;
    private final double z;
    private final double strength;
    
    KnockbackSourceImpl(final double x, final double z, final double strength) {
        this.x = x;
        this.z = z;
        this.strength = strength;
    }
    
    @Override
    public double x() {
        return x;
    }
    
    @Override
    public double z() {
        return z;
    }
    
    @Override
    public double strength() {
        return strength;
    }
    
}
