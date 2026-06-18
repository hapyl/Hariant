package me.hapyl.hariant.entity;

import me.hapyl.eterna.module.annotate.SelfReturn;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Buildable;
import me.hapyl.hariant.util.decimal.Decimal;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public final class VanillaAttribute implements Buildable<AttributeModifier> {
    
    private final Key key;
    private final Attribute attribute;
    
    private double amount;
    private Operation operation;
    
    private VanillaAttribute(@NotNull Key key, @NotNull Attribute attribute) {
        this.key = key;
        this.attribute = attribute;
        this.amount = 0;
        this.operation = Operation.FLAT;
    }
    
    @NotNull
    public Key getKey() {
        return key;
    }
    
    @NotNull
    public Attribute getAttribute() {
        return attribute;
    }
    
    @SelfReturn
    public VanillaAttribute amount(double amount) {
        this.amount = amount;
        return this;
    }
    
    @SelfReturn
    public VanillaAttribute amount(@NotNull Decimal amount) {
        return amount(amount.doubleValue());
    }
    
    @SelfReturn
    public VanillaAttribute operation(@NotNull Operation operation) {
        this.operation = operation;
        return this;
    }
    
    @NonNull
    @Override
    public AttributeModifier build() {
        return new AttributeModifier(key.asNamespacedKey(), amount, operation.bukkit);
    }
    
    @NotNull
    public static VanillaAttribute builder(@NotNull Key key, @NotNull Attribute attribute) {
        return new VanillaAttribute(key, attribute);
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
    
}