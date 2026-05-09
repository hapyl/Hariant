package me.hapyl.hariant.weapon;

import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.damage.KnockbackSource;
import org.jetbrains.annotations.NotNull;

public class NormalAttackRanged extends NormalAttack {
    
    public NormalAttackRanged(@NotNull ElementType elementType, @NotNull AttributeType attributeType, double scaling, int shotCooldown) {
        super(elementType, attributeType, scaling, shotCooldown);
    }
    
    @NotNull
    @Override
    public DamageSource.Builder createDamageSource(@NotNull HariantEntity attacker) {
        return super.createDamageSource(attacker)
                    .damageType(DamageType.RANGED);
    }
    
    @NotNull
    @Override
    public KnockbackSource createKnockbackCause(@NotNull HariantEntity attacker) {
        return KnockbackSource.create(attacker, HariantConstants.RANGE_KNOCKBACK_STRENGTH);
    }
}
