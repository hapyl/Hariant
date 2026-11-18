package me.hapyl.hariant.entity.damage.mutator;

import org.jetbrains.annotations.NotNull;

public interface DamageMutator {
    
    @NotNull String operator();
    
    double mutate(double damage, final double value);
    
    static @NotNull DamageMutator multiply() {
        return DamageMutatorImplMultiply.INSTANCE;
    }
    
    static @NotNull DamageMutator add() {
        return DamageMutatorImplAdd.INSTANCE;
    }
    
    static @NotNull DamageMutator subtract() {
        return DamageMutatorImplSubtract.INSTANCE;
    }
    
}