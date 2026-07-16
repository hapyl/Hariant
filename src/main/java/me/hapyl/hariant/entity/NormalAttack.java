package me.hapyl.hariant.entity;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.annotate.Singleton;
import me.hapyl.hariant.attribute.AttributeScalingSingle;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.weapon.NormalAttackRanged;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class NormalAttack extends AttributeScalingSingle implements DamageSourceCreator {
    
    private static final DamageSourceIdentity DAMAGE_SOURCE_IDENTITY = DamageSourceIdentity.create(Key.ofString("normal_attack"), Component.text("Normal Attack"), DeathMessage.DEFAULT);
    
    protected final ElementType elementType;
    protected final int attackCooldown;
    
    public NormalAttack(@NotNull ElementType elementType, @NotNull AttributeType attributeType, double attributeScaling, int attackCooldown) {
        super(attributeType, attributeScaling);
        
        this.elementType = elementType;
        this.attackCooldown = attackCooldown;
    }
    
    public int getAttackCooldown() {
        return attackCooldown;
    }
    
    @NotNull
    public ElementType getElementType() {
        return elementType;
    }
    
    @NotNull
    @Override
    public DamageSource.Builder createDamageSource(@NotNull HariantEntity attacker) {
        // DamageType default to Melee, so no need to explicitly set it
        return DamageSource.builder(DAMAGE_SOURCE_IDENTITY, getScaledValue(attacker))
                           .elementType(elementType)
                           .source(attacker)
                           .components(DamageComponent.ofCommon());
    }
    
    @NotNull
    public KnockbackSource createKnockbackCause(@NotNull HariantEntity attacker) {
        return KnockbackSource.create(attacker, HariantConstants.MELEE_KNOCKBACK_STRENGTH);
    }
    
    @NotNull
    public static NormalAttack melee(@NotNull ElementType elementType, @NotNull AttributeType attributeType, double attributeScaling, int attackCooldown) {
        return new NormalAttack(elementType, attributeType, attributeScaling, attackCooldown);
    }
    
    @NotNull
    public static NormalAttackRanged ranged(@NotNull ElementType elementType, @NotNull AttributeType attributeType, double attributeScaling, int shotCooldown) {
        return new NormalAttackRanged(elementType, attributeType, attributeScaling, shotCooldown);
    }
    
    @NotNull
    @Singleton
    public static NormalAttack common() {
        class Holder {
            private static final NormalAttack COMMON = new NormalAttack(ElementType.PHYSICAL, AttributeType.ATTACK, 100, 10);
        }
        
        return Holder.COMMON;
    }
    
}
