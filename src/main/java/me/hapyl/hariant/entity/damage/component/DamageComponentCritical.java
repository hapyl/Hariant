package me.hapyl.hariant.entity.damage.component;

import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesBase;
import me.hapyl.hariant.attribute.instance.AttributesInstanceSnapshot;
import me.hapyl.hariant.entity.damage.DamageInstance;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class DamageComponentCritical implements DamageComponent {
    
    @NotNull
    @Override
    public String identify() {
        return "critical";
    }
    
    @Override
    public double multiplier(@NotNull DamageInstance damageInstance, @NotNull AttributesInstanceSnapshot snapshotEntity, @NotNull AttributesInstanceSnapshot snapshotAttacker) {
        if (damageInstance.isAlreadyCritical()) {
            return 1.0;
        }
        
        final double critChance = getCritChance(snapshotAttacker);
        final Random random = Hariant.getRandom();
        
        if (critChance >= 1.0 || (critChance > 0.0 && random.nextDouble() < critChance)) {
            damageInstance.markCritical();
            return 1 + snapshotAttacker.normalized(AttributeType.CRIT_DAMAGE);
        }
        
        return 1.0;
    }
    
    public double getCritChance(@NotNull AttributesBase attributes) {
        return attributes.normalized(AttributeType.CRIT_CHANCE);
    }
    
}
