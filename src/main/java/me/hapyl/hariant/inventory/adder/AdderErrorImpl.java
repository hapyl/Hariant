package me.hapyl.hariant.inventory.adder;

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
