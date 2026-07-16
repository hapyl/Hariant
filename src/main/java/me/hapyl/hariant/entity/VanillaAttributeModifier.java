package me.hapyl.hariant.entity;

import me.hapyl.eterna.module.annotate.SelfReturn;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Buildable;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.jetbrains.annotations.NotNull;

public final class VanillaAttributeModifier {
    
    private final Attribute attribute;
    private final NamespacedKey modifierKey;
    private final AttributeModifier modifier;
    
    private VanillaAttributeModifier(@NotNull Key key, @NotNull Attribute attribute, @NotNull Operation operation, final double amount) {
        this.attribute = attribute;
        this.modifierKey = key.asNamespacedKey();
        this.modifier = new AttributeModifier(modifierKey, amount, operation.bukkit);
    }
    
    public @NotNull Attribute getAttribute() {
        return attribute;
    }
    
    public @NotNull NamespacedKey getModifierKey() {
        return modifierKey;
    }
    
    public @NotNull AttributeModifier getModifier() {
        return modifier;
    }
    
    public static @NotNull Builder builder(@NotNull Key key, @NotNull Attribute attribute) {
        return new Builder(key, attribute);
    }
    
    public static @NotNull VanillaAttributeModifier create(@NotNull Key key, @NotNull Attribute attribute, @NotNull Operation operation, final double value) {
        return new VanillaAttributeModifier(key, attribute, operation, value);
    }
    
    public enum Operation {
        FLAT(AttributeModifier.Operation.ADD_NUMBER),
        ADDITIVE(AttributeModifier.Operation.ADD_SCALAR),
        MULTIPLICATIVE(AttributeModifier.Operation.MULTIPLY_SCALAR_1);
        
        private final AttributeModifier.Operation bukkit;
        
        Operation(@NotNull AttributeModifier.Operation bukkit) {
            this.bukkit = bukkit;
        }
    }
    
    public static class Builder implements Buildable<VanillaAttributeModifier> {
        
        private final @NotNull Key key;
        private final @NotNull Attribute attribute;
        
        private @NotNull Operation operation;
        private double value;
        
        Builder(@NotNull Key key, @NotNull Attribute attribute) {
            this.key = key;
            this.attribute = attribute;
            this.operation = Operation.FLAT;
            this.value = 0;
        }
        
        @SelfReturn
        public Builder operation(@NotNull Operation operation) {
            this.operation = operation;
            return this;
        }
        
        @SelfReturn
        public Builder value(double value) {
            this.value = value;
            return this;
        }
        
        @Override
        public @NotNull VanillaAttributeModifier build() {
            return new VanillaAttributeModifier(key, attribute, operation, value);
        }
    }
    
}