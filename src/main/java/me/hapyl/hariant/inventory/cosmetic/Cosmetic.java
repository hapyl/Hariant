package me.hapyl.hariant.inventory.cosmetic;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.inventory.item.AbstractItem;
import me.hapyl.hariant.inventory.item.ItemCategory;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class Cosmetic extends AbstractItem {
    
    private final CosmeticType cosmeticType;
    
    public Cosmetic(@NotNull Key key, @NotNull Component name, @NotNull Icon icon, @NotNull CosmeticType cosmeticType) {
        super(key, name, icon);
        
        this.cosmeticType = cosmeticType;
        this.category = ItemCategory.COSMETIC;
    }
    
}
