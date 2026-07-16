package me.hapyl.hariant.menu;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import org.jetbrains.annotations.NotNull;

public interface Button {
    
    @NotNull ItemBuilder createBuilder();
    
    @NotNull PlayerMenuAction createMenuAction();
    
    static @NotNull Button create(@NotNull ItemBuilder builder, @NotNull PlayerMenuAction menuAction) {
        return new ButtonImpl(builder, menuAction);
    }
    
    static @NotNull Button create(@NotNull ItemBuilder builder) {
        return new ButtonImpl(builder, PlayerMenuAction.of(_ -> {}));
    }
    
}
