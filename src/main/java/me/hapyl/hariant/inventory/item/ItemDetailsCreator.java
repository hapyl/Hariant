package me.hapyl.hariant.inventory.item;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import org.jetbrains.annotations.NotNull;

public interface ItemDetailsCreator {
    
    @NotNull ItemBuilder createDetailsBuilder();
    
}
