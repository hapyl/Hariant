package me.hapyl.hariant.entity.heal;

import me.hapyl.hariant.entity.HariantEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HealingSourceImpl implements HealingSource {
    
    private final double amount;
    private final Component name;
    private final HariantEntity healer;
    
    HealingSourceImpl(double amount, @NotNull Component name, @Nullable HariantEntity healer) {
        this.amount = amount;
        this.name = name;
        this.healer = healer;
    }
    
    @Override
    public double getAmount() {
        return amount;
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Nullable
    @Override
    public HariantEntity getHealer() {
        return healer;
    }
    
}
