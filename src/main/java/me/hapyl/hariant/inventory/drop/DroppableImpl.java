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
    
    @NotNull
    @Override
    public final Component getName() {
        return name;
    }
    
    @Range(from = 0, to = Integer.MAX_VALUE)
    @Override
    public final int getWeight() {
        return weight;
    }
    
    @NotNull
    @Override
    public final Amount getAmount() {
        return amount;
    }
    
}