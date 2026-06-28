package me.hapyl.hariant.inventory.drop;

import me.hapyl.eterna.module.registry.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public abstract class DroppableImpl implements Droppable {
    
    private final Key key;
    private final int weight;
    private final Component name;
    private final Amount amount;
    
    DroppableImpl(@NotNull Key key, final int weight, @NotNull Component name, @NotNull Amount amount) {
        this.key = key;
        this.weight = weight;
        this.name = name;
        this.amount = amount;
    }
    
    @Override
    public final @NotNull Key getKey() {
        return key;
    }
    
    @Override
    public final @NotNull Component getName() {
        return name;
    }
    
    @Override
    public final @NotNull Amount getAmount() {
        return amount;
    }
    
    @Range(from = 0, to = Integer.MAX_VALUE)
    @Override
    public final int getWeight() {
        return weight;
    }
    
}