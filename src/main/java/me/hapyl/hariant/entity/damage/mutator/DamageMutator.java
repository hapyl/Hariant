package me.hapyl.hariant.entity.damage.mutator;

import me.hapyl.hariant.util.Identified;
import org.jetbrains.annotations.NotNull;

public interface DamageMutator extends Identified {
    
    @NotNull
    @Override
    String identify();
    
    double mutate(double damage, final double value);
    
    @NotNull
    static DamageMutator multiply() {
        return Holder.MULTIPLY;
    }
    
    @NotNull
    static DamageMutator add() {
        return Holder.ADD;
    }
    
    @NotNull
    static DamageMutator subtract() {
        return Holder.SUBTRACT;
    }
    
    class Holder {
        public static final DamageMutator MULTIPLY = new DamageMutatorImplMultiply();
        public static final DamageMutator ADD = new DamageMutatorImplAdd();
        public static final DamageMutator SUBTRACT = new DamageMutatorImplSubtract();
    }
    
}