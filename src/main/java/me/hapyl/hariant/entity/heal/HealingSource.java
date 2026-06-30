package me.hapyl.hariant.entity.heal;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.hariant.entity.HariantEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public interface HealingSource extends Named {
    
    @Range(from = 0, to = Integer.MAX_VALUE)
    double getAmount();
    
    @Override
    @NotNull Component getName();
    
    @Nullable HariantEntity getHealer();
    
    @NotNull
    static HealingSource create(double amount, @NotNull Component name, @Nullable HariantEntity healer) {
        return new HealingSourceImpl(amount, name, healer);
    }
    
    @NotNull
    static HealingSource create(double amount, @NotNull Named named, @Nullable HariantEntity healer) {
        return create(amount, named.getName(), healer);
    }
    
    @NotNull
    static HealingSource create(double amount, @NotNull Component name) {
        return create(amount, name, null);
    }
    
    @NotNull
    static HealingSource create(double amount, @NotNull Named named) {
        return create(amount, named.getName(), null);
    }
    
}
