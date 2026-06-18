package me.hapyl.hariant.talent.ultimate;

import org.jetbrains.annotations.NotNull;

public enum ChargeLevel {
    
    NORMAL,
    OVERCHARGED;
    
    public boolean isNormal() {
        return this == NORMAL;
    }
    
    public boolean isOvercharged() {
        return this == OVERCHARGED;
    }
    
    public <K> @NotNull K either(@NotNull K normal, @NotNull K overcharged) {
        return this == NORMAL ? normal : overcharged;
    }
    
}
