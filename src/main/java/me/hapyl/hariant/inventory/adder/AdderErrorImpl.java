package me.hapyl.hariant.inventory.adder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public final class AdderErrorImpl implements AdderError {
    
    private final String string;
    
    AdderErrorImpl(@NotNull String string) {
        this.string = string;
    }
    
    @Override
    public @NotNull String getError() {
        return string;
    }
    
}
