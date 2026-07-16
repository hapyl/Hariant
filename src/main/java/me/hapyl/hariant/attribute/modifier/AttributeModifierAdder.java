package me.hapyl.hariant.attribute.modifier;

import me.hapyl.eterna.module.annotate.SelfReturn;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.util.decimal.Decimal;
import org.jetbrains.annotations.NotNull;

public interface AttributeModifierAdder {

    @SelfReturn
    AttributeModifierAdder of(@NotNull AttributeType attributeType, @NotNull AttributeModifierType modifierType, double value);
    
    @NotNull
    default AttributeModifierAdder of(@NotNull AttributeType attributeType, @NotNull AttributeModifierType modifierType, @NotNull Decimal value) {
        return of(attributeType, modifierType, value.doubleValue());
    }
    
    @SelfReturn
    AttributeModifierAdder ofElementalDamageBonus(@NotNull AttributeModifierType modifierType, double value);
    
    @SelfReturn
    AttributeModifierAdder ofElementalResistance(@NotNull AttributeModifierType modifierType, double value);

}
