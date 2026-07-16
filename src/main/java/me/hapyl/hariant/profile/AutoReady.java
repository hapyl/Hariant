package me.hapyl.hariant.profile;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.text.Capitalizable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public enum AutoReady implements ComponentLike, Described {
    
    NEVER(Component.text("Never ready automatically.")),
    ALWAYS(Component.text("Always ready automatically.")),
    ALWAYS_EXCEPT_ON_JOIN(Component.text("Always ready automatically, except when you join the server."));
    
    private final Component component;
    private final Component description;
    
    AutoReady(@NotNull Component description) {
        this.component = Component.text(Capitalizable.capitalize(this));
        this.description = description;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return component;
    }
    
    @Override
    public @NotNull Component getDescription() {
        return description;
    }
    
}