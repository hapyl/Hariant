package me.hapyl.hariant.entity.damage.component;

import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.snapshot.AttributesSnapshot;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.damage.DamageInstance;
import org.jetbrains.annotations.NotNull;

public final class DamageComponentElementalDamageBonus implements DamageComponent {
    
    DamageComponentElementalDamageBonus() {
    }
    
    @Override
    public @NotNull String identify() {
        return "Elemental DMG";
    }
    
    @Override
    public double multiplier(@NotNull DamageInstance damageInstance, @NotNull AttributesSnapshot entity, @NotNull AttributesSnapshot attacker) {
        final ElementType elementType = damageInstance.getElementType();
        final AttributeType attributeType = elementType.getOffensiveAttribute();
        
        if (attributeType == null) {
            return 1;
        }
        
        return 1 + attacker.get(attributeType) / 100;
    }
    
}
