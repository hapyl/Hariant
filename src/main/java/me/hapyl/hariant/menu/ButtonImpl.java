package me.hapyl.hariant.menu;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import org.jetbrains.annotations.NotNull;

public class ButtonImpl implements Button {
    
    private final ItemBuilder itemBuilder;
    private final PlayerMenuAction menuAction;
    
    ButtonImpl(@NotNull ItemBuilder itemBuilder, @NotNull PlayerMenuAction menuAction) {
        this.itemBuilder = itemBuilder;
        this.menuAction = menuAction;
    }
    
    @Override
    public @NotNull ItemBuilder createBuilder() {
        return itemBuilder;
    }
    
    @Override
    public @NotNull PlayerMenuAction createMenuAction() {
        return menuAction;
    }
    
}