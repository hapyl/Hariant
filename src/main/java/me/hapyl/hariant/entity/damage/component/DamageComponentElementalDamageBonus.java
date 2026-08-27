package me.hapyl.hariant.entity.damage.component;

import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstanceSnapshot;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.damage.DamageInstance;
import org.jetbrains.annotations.NotNull;

public final class DamageComponentElementalDamageBonus implements DamageComponent {
    
    DamageComponentElementalDamageBonus() {
    }
    
    @NotNull
    @Override
    public String identify() {
        return "Elemental DMG";
    }
    
    @Override
    public double multiplier(@NotNull DamageInstance damageInstance, @NotNull AttributesInstanceSnapshot entity, @NotNull AttributesInstanceSnapshot attacker) {
        final ElementType elementType = damageInstance.getDamageSource().getElementType();
        final AttributeType attributeType = elementType.getOffensiveAttribute();
        
        if (attributeType == null) {
            return 1;
        }
        
        return 1 + attacker.get(attributeType) / 100;
    }
    
}
