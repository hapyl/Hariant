package me.hapyl.hariant.entity;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public enum WarningType implements ComponentLike {
    
    WARNING(Component.text("⚠", NamedTextColor.GOLD)),
    DANGER(Component.text("⚠", NamedTextColor.DARK_RED));
    
    private final Component component;
    
    WarningType(@NotNull Component component) {
        this.component = component;
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        return component;
    }
}
