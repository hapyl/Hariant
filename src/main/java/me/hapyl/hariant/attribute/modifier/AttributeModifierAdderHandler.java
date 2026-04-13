package me.hapyl.hariant.attribute.modifier;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface AttributeModifierAdderHandler {
    
    void handle(@NotNull AttributeModifierAdder adder);
    
}
