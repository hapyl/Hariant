package me.hapyl.hariant.entity.damage.component;

import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstanceSnapshot;
import me.hapyl.hariant.entity.damage.DamageInstance;
import org.jetbrains.annotations.NotNull;

public final class DamageComponentDefense implements DamageComponent {
    
    DamageComponentDefense() {
    }
    
    @NotNull
    @Override
    public String identify() {
        return "defense";
    }
    
    @Override
    public double multiplier(@NotNull DamageInstance damageInstance, @NotNull AttributesInstanceSnapshot entity, @NotNull AttributesInstanceSnapshot attacker) {
        final double defense = entity.get(AttributeType.DEFENSE);
        
        return HariantConstants.DEFENSE_DIVISOR / (defense + HariantConstants.DEFENSE_DIVISOR);
    }
    
}
