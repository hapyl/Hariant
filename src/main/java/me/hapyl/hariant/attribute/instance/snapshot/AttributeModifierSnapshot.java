package me.hapyl.hariant.attribute.instance.snapshot;

import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.util.decimal.Decimal;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class AttributeModifierSnapshot {
    
    private final Map<AttributeType, Double> multiplierMap;
    
    AttributeModifierSnapshot(@NotNull Map<AttributeType, Double> multiplierMap) {
        this.multiplierMap = multiplierMap;
    }
    
    public double getMultiplier(@NotNull AttributeType attributeType) {
        return multiplierMap.getOrDefault(attributeType, 0.0);
    }
    
    public static @NotNull AttributeModifierSnapshot create(@NotNull AttributeType attributeType, final double value) {
        return new AttributeModifierSnapshot(Map.of(attributeType, value));
    }
    
    public static @NotNull AttributeModifierSnapshot create(@NotNull AttributeType attributeType, @NotNull Decimal value) {
        return create(attributeType, value.doubleValue());
    }
    
    public static @NotNull AttributeModifierSnapshot create(@NotNull Map<AttributeType, Double> attributeModifiers) {
        return new AttributeModifierSnapshot(attributeModifiers);
    }
    
}