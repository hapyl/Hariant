package me.hapyl.hariant.entity.damage.mutator;

import me.hapyl.hariant.util.Identified;
import org.jetbrains.annotations.NotNull;

public interface DamageMutator extends Identified {
    
    @NotNull
    @Override
    String identify();
    
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