package me.hapyl.hariant.attribute.instance;

import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import org.jetbrains.annotations.NotNull;

public interface AttributesBase {
    double get(@NotNull AttributeType attributeType);
    
    default double normalized(@NotNull AttributeType attributeType) {
        return this.get(attributeType) / 100;
    }
    
    double base(@NotNull AttributeType attributeType);
    
    default double getElementalDamageBonus(@NotNull ElementType elementType) {
        return this.get(AttributeType.getElementalDamageBonusAttribute(elementType));
    }
    
    default double getElementalResistance(@NotNull ElementType elementType) {
        return this.get(AttributeType.getElementalResistanceAttribute(elementType));
    }
    
    void set(@NotNull AttributeType attributeType, double value);
}
