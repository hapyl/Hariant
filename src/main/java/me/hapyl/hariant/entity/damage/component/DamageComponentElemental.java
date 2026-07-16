package me.hapyl.hariant.entity.damage.component;

import me.hapyl.eterna.module.util.Nulls;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstanceSnapshot;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.damage.DamageInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DamageComponentElemental implements DamageComponent {
    
    DamageComponentElemental() {
    }
    
    @NotNull
    @Override
    public String identify() {
        return "elemental_bonus";
    }
    
    @Override
    public double multiplier(@NotNull DamageInstance damageInstance, @NotNull AttributesInstanceSnapshot entity, @NotNull AttributesInstanceSnapshot attacker) {
        final ElementType element = damageInstance.getSource().getElementType();
        
        final @Nullable AttributeType offensiveAttribute = element.getOffensiveAttribute();
        final @Nullable AttributeType defensiveAttribute = element.getDefensiveAttribute();
       
        final double elementalDamageBonus = Nulls.unwrap(offensiveAttribute, attacker::get, 0.0);
        final double elementalResistance = Nulls.unwrap(defensiveAttribute, attacker::get, 0.0);
        
        return 1 + (elementalDamageBonus / 100 - elementalResistance / 100);
    }
    
}
