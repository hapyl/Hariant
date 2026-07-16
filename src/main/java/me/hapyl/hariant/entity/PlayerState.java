package me.hapyl.hariant.entity;

import me.hapyl.hariant.Colors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public enum PlayerState implements ComponentLike {
    
    ALIVE(Component.text("Alive", Colors.SUCCESS)),
    DEAD(Component.text("Dead", Colors.ERROR)),
    RESPAWNING(Component.text("Respawning", Colors.YELLOW));
    
    private final Component component;
    
    PlayerState(@NotNull Component component) {
        this.component = component;
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        return component;
    }
}
