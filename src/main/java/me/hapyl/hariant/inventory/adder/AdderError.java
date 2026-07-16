package me.hapyl.hariant.inventory.adder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public interface AdderError extends ComponentLike {
    
    @NotNull AdderError INVENTORY_FULL = create("Not enough inventory space!");
    
    @NotNull String getError();
    
    @Override
    default @NotNull Component asComponent() {
        return Component.text(this.getError());
    }
    
    static @NotNull AdderError create(@NotNull String errorMessage) {
        return new AdderErrorImpl(errorMessage);
    }
    
}