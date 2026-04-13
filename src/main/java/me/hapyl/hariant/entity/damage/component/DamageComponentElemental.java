package me.hapyl.hariant.entity.damage.component;

import me.hapyl.hariant.attribute.instance.AttributesInstanceSnapshot;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.damage.DamageInstance;
import org.jetbrains.annotations.NotNull;

public class DamageComponentElemental implements DamageComponent {
    
    @NotNull
    @Override
    public String identify() {
        return "elemental_bonus";
    }
    
    @Override
    public double multiplier(@NotNull DamageInstance damageInstance, @NotNull AttributesInstanceSnapshot snapshotEntity, @NotNull AttributesInstanceSnapshot snapshotAttacker) {
        final ElementType elementType = damageInstance.getSource().getElementType();
        
        final double elementalDamageBonus = snapshotAttacker.getElementalDamageBonus(elementType);
        final double elementalResistance = snapshotEntity.getElementalResistance(elementType);
        
        return 1 + (elementalDamageBonus / 100 - elementalResistance / 100);
    }
    
}
