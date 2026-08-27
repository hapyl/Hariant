package me.hapyl.hariant.game.battleground;

import me.hapyl.eterna.module.component.Named;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public enum Size implements Named {
    
    SMALL(Component.text("Small")),
    MEDIUM(Component.text("Medium")),
    LARGE(Component.text("Large")),
    MASSIVE(Component.text("Massive"));
    
    private final Component name;
    
    Size(@NotNull Component name) {
        this.name = name;
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
}
