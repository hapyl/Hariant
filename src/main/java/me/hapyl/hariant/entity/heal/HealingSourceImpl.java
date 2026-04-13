package me.hapyl.hariant.entity.heal;

import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.Nullable;

public class HealingSourceImpl implements HealingSource {
    
    private final double amount;
    private final HariantEntity healer;
    
    HealingSourceImpl(double amount, @Nullable HariantEntity healer) {
        this.amount = amount;
        this.healer = healer;
    }
    
    @Override
    public double amount() {
        return amount;
    }
    
    @Nullable
    @Override
    public HariantEntity healer() {
        return healer;
    }
    
}
