package me.hapyl.hariant.entity;

import me.hapyl.hariant.Colors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public enum WarningType implements ComponentLike {
    
    WARNING(Component.text("⚠", Colors.GOLD)),
    DANGER(Component.text("⚠", Colors.DARK_RED));
    
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
