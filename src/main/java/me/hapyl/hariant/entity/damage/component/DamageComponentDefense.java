package me.hapyl.hariant.entity.damage.component;

import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.snapshot.AttributesSnapshot;
import me.hapyl.hariant.entity.damage.DamageInstance;
import org.jetbrains.annotations.NotNull;

public final class DamageComponentDefense implements DamageComponent {
    
    DamageComponentDefense() {
    }
    
    @Override
    public @NotNull String identify() {
        return "Defense";
    }
    
    @Override
    public double multiplier(@NotNull DamageInstance damageInstance, @NotNull AttributesSnapshot entity, @NotNull AttributesSnapshot attacker) {
        final double defense = entity.get(AttributeType.DEFENSE);
        
        return HariantConstants.DEFENSE_DIVISOR / (defense + HariantConstants.DEFENSE_DIVISOR);
    }
    
}
