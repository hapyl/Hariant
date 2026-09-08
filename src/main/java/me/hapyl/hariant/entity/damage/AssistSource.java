package me.hapyl.hariant.entity.damage;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.hariant.entity.HariantEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface AssistSource extends Named {
    
    @NotNull HariantEntity source();
    
    @Override
    @NotNull Component getName();
    
    static @NotNull AssistSource create(@NotNull HariantEntity source, @NotNull Component name) {
        return new AssistSourceImpl(source, name);
    }
    
    static @NotNull AssistSource create(@NotNull HariantEntity source, @NotNull Named named) {
        return create(source, named.getName());
    }
    
}
