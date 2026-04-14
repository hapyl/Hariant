package me.hapyl.hariant.entity.damage;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface AssistSource extends Named {
    
    @Override
    @NotNull
    Component getName();
    
    @NotNull
    static AssistSource create(@NotNull Component name) {
        return new AssistSourceImpl(name);
    }
    
    @NotNull
    static AssistSource create(@NotNull Named named) {
        return create(named.getName());
    }
    
}
