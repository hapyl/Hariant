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
    
    @Override
    public @NotNull DamageSource createDamageSource(@NotNull HariantEntity attacker) {
        final DamageSource damageSource = super.createDamageSource(attacker);
        
        // Very important that the damage type is set to RANGED, especially for bows
        damageSource.setDamageType(DamageType.RANGED);
        
        return damageSource;
    }
    
    @NotNull
    @Override
    public KnockbackSource createKnockbackCause(@NotNull HariantEntity attacker) {
        return KnockbackSource.create(attacker, HariantConstants.RANGE_KNOCKBACK_STRENGTH);
    }
}
