package me.hapyl.hariant.entity.damage.component;

import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstanceSnapshot;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.damage.DamageInstance;
import org.jetbrains.annotations.NotNull;

public final class DamageComponentElementalResistance implements DamageComponent {
    
    private static final double RESISTANCE_FALL_OFF_THRESHOLD = 60;
    private static final double RESISTANCE_FALL_OFF_COEFFICIENT = 1 / (1 - RESISTANCE_FALL_OFF_THRESHOLD / 100);
    
    DamageComponentElementalResistance() {
    }
    
    @Override
    public @NotNull String identify() {
        return "Elemental RES";
    }
    
    @Override
    public double multiplier(@NotNull DamageInstance damageInstance, @NotNull AttributesInstanceSnapshot entity, @NotNull AttributesInstanceSnapshot attacker) {
        final ElementType elementType = damageInstance.getElementType();
        final AttributeType defensiveAttribute = elementType.getDefensiveAttribute();
        
        if (defensiveAttribute == null) {
            return 1;
        }
        
        return resistanceMultiplier(entity.get(defensiveAttribute));
    }
    
    public static double resistanceMultiplier(final double resistance) {
        // For negative values, resistance is halved
        if (resistance < 0) {
            return 1 - resistance / 100 * 0.5;
        }
        // For values below falloff threshold, the resistance is as is
        else if (resistance < RESISTANCE_FALL_OFF_THRESHOLD) {
            return 1 - resistance / 100;
        }
        // For values above the falloff threshold, the resistance is using diminishings returns
        else {
            return 1 / (RESISTANCE_FALL_OFF_COEFFICIENT * resistance / 100 + 1);
        }
    }
    
}