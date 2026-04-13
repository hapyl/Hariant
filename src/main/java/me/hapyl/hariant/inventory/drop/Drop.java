package me.hapyl.hariant.inventory.drop;

import me.hapyl.eterna.module.component.Named;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public final class Drop implements Named, ComponentLike {
    
    private final Component name;
    private final int amount;
    
    private final Component component;
    
    Drop(@NotNull Droppable droppable, int amount) {
        this.name = droppable.getName();
        this.amount = amount;
        this.component = Component.empty()
                                  .append(Component.text("%d".formatted(amount), NamedTextColor.GOLD))
                                  .append(Component.text(" x ", NamedTextColor.DARK_GRAY))
                                  .append(name);
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    public int getAmount() {
        return amount;
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        return component;
    }
    
}