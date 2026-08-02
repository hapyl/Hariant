package me.hapyl.hariant.game;

import me.hapyl.eterna.module.component.Named;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public enum WinType implements Named {
    
    WIN_CONDITION_MET(Component.text("Win Condition Met")),
    TIME_LIMIT(Component.text("Time Limit")),
    COMMAND(Component.text("Command"));
    
    private final Component name;
    
    WinType(@NotNull Component name) {
        this.name = name;
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
}
