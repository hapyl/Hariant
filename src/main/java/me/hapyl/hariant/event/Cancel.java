package me.hapyl.hariant.event;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public final class Cancel implements ComponentLike {
    
    private static final Cancel CANCEL_DEFAULT = cancel(Component.text("Cannot do that right now!"));
    
    private final Component reason;
    
    Cancel(@NotNull Component reason) {
        this.reason = reason;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return reason;
    }
    
    public static @NotNull Cancel cancel(@NotNull Component reason) {
        return new Cancel(reason);
    }
    
    public static @NotNull Cancel cancelDefault() {
        return CANCEL_DEFAULT;
    }
    
}