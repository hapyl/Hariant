package me.hapyl.hariant.entity.damage.component;

import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstanceSnapshot;
import me.hapyl.hariant.entity.damage.DamageFlag;
import me.hapyl.hariant.entity.damage.DamageInstance;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public final class DamageComponentCritical implements DamageComponent {
    
    DamageComponentCritical() {
    }
    
    @NotNull
    @Override
    public String identify() {
        return "critical";
    }
    
    @Override
    public double multiplier(@NotNull DamageInstance damageInstance, @NotNull AttributesInstanceSnapshot entity, @NotNull AttributesInstanceSnapshot attacker) {
        if (damageInstance.isCritical()) {
            return 1.0;
        }
        
        final double critChance = attacker.normalized(AttributeType.CRIT_CHANCE);
        final boolean forceCritical = damageInstance.getDamageSource().isFlagged(DamageFlag.FORCE_CRITICAL);
        
        final Random random = Hariant.getRandom();
        
        if (critChance >= 1.0 || forceCritical || (critChance > 0.0 && random.nextDouble() < critChance)) {
            damageInstance.markCritical();
            return 1 + attacker.normalized(AttributeType.CRIT_DAMAGE);
        }
        
        return 1.0;
    }
    
}
