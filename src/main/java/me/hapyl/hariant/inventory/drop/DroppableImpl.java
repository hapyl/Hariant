package me.hapyl.hariant.inventory.drop;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public abstract class DroppableImpl implements Droppable {
    
    private final int weight;
    private final Component name;
    private final Amount amount;
    
    DroppableImpl(final int weight, @NotNull Component name, @NotNull Amount amount) {
        this.weight = weight;
        this.name = name;
        this.amount = amount;
    }
    
    @Range(from = 0, to = Integer.MAX_VALUE)
    @Override
    public int getWeight() {
        return weight;
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @NotNull
    @Override
    public Amount getAmount() {
        return amount;
    }
}
