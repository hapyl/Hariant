package me.hapyl.hariant.inventory.item;

import me.hapyl.eterna.module.component.Named;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public enum ItemCategory implements Named {
    
    ARTIFACT(Component.text("Artifacts"), 1000),
    ACCOUNT_RESOURCE(Component.text("Resources"), 500),
    COSMETIC(Component.text("Cosmetics"), 100),
    
    ;
    
    private final Component name;
    private final int capacity;
    
    ItemCategory(@NotNull Component artifacts, int capacity) {
        this.name = artifacts;
        this.capacity = capacity;
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
}
