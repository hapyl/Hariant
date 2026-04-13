package me.hapyl.hariant.entity.heal;

import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public interface HealingSource {
    
    @Range(from = 0, to = Integer.MAX_VALUE)
    double amount();
    
    @Nullable
    HariantEntity healer();
    
    @NotNull
    static HealingSource create(double amount, @Nullable HariantEntity healer) {
        return new HealingSourceImpl(amount, healer);
    }
    
    @NotNull
    static HealingSource create(double amount) {
        return create(amount, null);
    }
    
}
