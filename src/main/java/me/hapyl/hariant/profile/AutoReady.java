package me.hapyl.hariant.profile;

import me.hapyl.eterna.module.text.Capitalizable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public enum AutoReady implements ComponentLike {
    
    NEVER,
    ALWAYS,
    ALWAYS_EXCEPT_ON_JOIN;
    
    private final Component component;
    
    AutoReady() {
        this.component = Component.text(Capitalizable.capitalize(this));
    }
    
    @Override
    public @NotNull Component asComponent() {
        return component;
    }
    
}