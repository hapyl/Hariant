package me.hapyl.hariant.inventory.item;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface ItemCreator {
    
    @NotNull ItemBuilder createBuilder();
    
    default @NotNull ItemStack createItem() {
        return createBuilder().asIcon();
    }
    
    default @NotNull ItemStack createIcon() {
        return createBuilder().setHideTooltip(true).asIcon();
    }
    
}
