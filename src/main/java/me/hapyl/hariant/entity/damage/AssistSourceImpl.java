package me.hapyl.hariant.entity.damage;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class AssistSourceImpl implements AssistSource {
    
    private final Component name;
    
    AssistSourceImpl(@NotNull Component name) {
        this.name = name;
    }
    
    @Override
    @NotNull
    public Component getName() {
        return name;
    }
    
}
