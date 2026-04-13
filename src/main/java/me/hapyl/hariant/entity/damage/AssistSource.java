package me.hapyl.hariant.entity.damage;

import me.hapyl.eterna.module.component.Named;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface AssistSource extends Named {
    
    @Override
    @NotNull
    Component getName();
    
    static AssistSource create(@NotNull Component name) {
        return new AssistSourceImpl(name);
    }
    
}
