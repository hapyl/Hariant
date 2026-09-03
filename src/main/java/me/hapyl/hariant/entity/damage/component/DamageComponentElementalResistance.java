package me.hapyl.hariant.entity.damage.component;

import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstanceSnapshot;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.damage.DamageInstance;
import org.jetbrains.annotations.NotNull;

public final class DamageComponentElementalResistance implements DamageComponent {
    
    private static final double MULTIPLIER_0 = 0.5;   // RES below 0 gets halved
    private static final double MULTIPLIER_75 = 0.25; // RES above 75 gets quartered
    
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
        
        // In order to balance elemental resistance, it uses conditional formula, where if the value of resistance is lower than 0,
        // it is halved, at the same time, it the value is higher than 75, it is quartered instead
        double value = entity.get(defensiveAttribute);
        
        if (value < 0) {
            value *= MULTIPLIER_0;
        }
        else if (value > 75) {
            value *= MULTIPLIER_75;
        }
        
        return 1 - value / 100;
    }
    
}