package me.hapyl.hariant.entity.damage.mutator;

import org.jetbrains.annotations.NotNull;

public final class DamageMutatorImplMultiply implements DamageMutator {
    
    DamageMutatorImplMultiply() {
    }
    
    @NotNull
    @Override
    public String identify() {
        return "*";
    }
    
    @Override
    public double mutate(double damage, double value) {
        return damage * value;
    }
    
}
