package me.hapyl.hariant.util;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import org.jetbrains.annotations.NotNull;

public interface LoreSupplier {
    
    void supplyLore(@NotNull ItemBuilder builder);
    
}
