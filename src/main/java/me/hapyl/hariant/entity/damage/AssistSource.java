package me.hapyl.hariant.entity.damage;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.hariant.entity.HariantEntity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface AssistSource extends Named {
    
    @NotNull
    HariantEntity source();
    
    @Override
    @NotNull
    Component getName();
    
    @NotNull
    static AssistSource create(@NotNull HariantEntity source, @NotNull Component name) {
        return new AssistSourceImpl(source, name);
    }
    
    @NotNull
    static AssistSource create(@NotNull HariantEntity source, @NotNull Named named) {
        return create(source, named.getName());
    }
    
}
