package me.hapyl.hariant.entity;

import org.jetbrains.annotations.NotNull;

public enum ImmunityResult {
    
    NOT_IMMUNE,
    IMMUNE,
    IMMUNE_SILENT;
    
    public boolean isImmune() {
        return this == IMMUNE || this == IMMUNE_SILENT;
    }
    
    public boolean isSilent() {
        return this == IMMUNE_SILENT;
    }
    
    @NotNull
    public static ImmunityResult ofBoolean(boolean condition) {
        return condition ? IMMUNE : NOT_IMMUNE;
    }
    
    @NotNull
    public static ImmunityResult ofBooleanSilent(boolean condition) {
        return condition ? IMMUNE_SILENT : NOT_IMMUNE;
    }
    
}
