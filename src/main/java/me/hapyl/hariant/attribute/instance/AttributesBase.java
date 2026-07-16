package me.hapyl.hariant.attribute.instance;

import me.hapyl.hariant.attribute.AttributeType;
import org.jetbrains.annotations.NotNull;

public interface AttributesBase {
    double get(@NotNull AttributeType attributeType);
    
    default double normalized(@NotNull AttributeType attributeType) {
        return this.get(attributeType) / 100;
    }
    
    double base(@NotNull AttributeType attributeType);
    
    void set(@NotNull AttributeType attributeType, double value);
    
    void add(@NotNull AttributeType attributeType, double value);
    
    default void subtract(@NotNull AttributeType attributeType, double value) {
        add(attributeType, -value);
    }
}
