package me.hapyl.hariant.inventory.cosmetic;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public enum CosmeticType implements Named, Described {
    
    ELIMINATION(
            Component.text("Elimination"),
            Component.text("Triggers whenever you eliminate another player.")
    ),
    
    DEATH(
            Component.text("Death"),
            Component.text("Triggers whenever you die.")
    ),
    
    
    ;
    
    private final Component name;
    private final Component description;
    
    CosmeticType(@NotNull Component name, @NotNull Component description) {
        this.name = name;
        this.description = description;
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public @NotNull Component getDescription() {
        return description;
    }
    
}