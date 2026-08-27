package me.hapyl.hariant.entity.damage;

import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.util.Identified;
import me.hapyl.hariant.util.decimal.Decimal;
import org.jetbrains.annotations.NotNull;

public interface MutatesDamage {
    
    void mutateDamage(@NotNull Identified identity, @NotNull DamageMutator mutator, double value);
    
    void mutateDamage(@NotNull Identified identity, @NotNull DamageMutator mutator, @NotNull Decimal value);
    
}
