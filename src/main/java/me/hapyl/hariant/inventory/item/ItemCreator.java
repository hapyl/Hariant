package me.hapyl.hariant.inventory.item;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface ItemCreator {
    
    @NotNull
    ItemBuilder createBuilder();
    
    @NotNull
    default ItemStack createItem() {
        return createBuilder().asIcon();
    }
    
    @NotNull
    default ItemStack createIcon() {
        return createBuilder().setHideTooltip(true).asIcon();
    }
    
}
