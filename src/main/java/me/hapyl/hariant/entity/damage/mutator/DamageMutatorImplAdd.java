package me.hapyl.hariant.entity.damage.mutator;

import org.jetbrains.annotations.NotNull;

public final class DamageMutatorImplAdd implements DamageMutator {
    
    static final @NotNull DamageMutator INSTANCE = new DamageMutatorImplAdd();
    
    private DamageMutatorImplAdd() {
    }
    
    @NotNull
    @Override
    public String identify() {
        return "+";
    }
    
    @Override
    public double mutate(double damage, double value) {
        return damage + value;
    }
}
