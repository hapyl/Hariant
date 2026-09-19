package me.hapyl.hariant.entity;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.annotate.Singleton;
import me.hapyl.hariant.attribute.AttributeScalingSingle;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponents;
import me.hapyl.hariant.weapon.NormalAttackRanged;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class NormalAttack extends AttributeScalingSingle implements DamageSourceCreator {
    
    private static final DamageSourceIdentity DEFAULT_DAMAGE_SOURCE_IDENTITY = DamageSourceIdentity.create(Key.ofString("normal_attack"), Component.text("Normal Attack"), DeathMessage.DEFAULT);
    private static final NormalAttack COMMON = new NormalAttack(ElementType.PHYSICAL, AttributeType.ATTACK, 100, 10);
    
    private static final Component COMPONENT_NONE = Component.text("None!", Colors.DARK_GRAY);
    
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
    
    public @NotNull ElementType getElementType() {
        return elementType;
    }
    
    public @NotNull DamageSourceIdentity getDamageSourceIdentity() {
        return DEFAULT_DAMAGE_SOURCE_IDENTITY;
    }
    
    @Override
    public @NotNull DamageSource.Builder createDamageSource(@NotNull HariantEntity attacker) {
        return DamageSource.builder(this.getDamageSourceIdentity(), this.getScaledValue(attacker))
                           // Default the attacker to being the source
                           .source(attacker)
                           // Default the damage type to MELEE
                           .damageType(DamageType.MELEE)
                           // Default the element type to the scaling's element type
                           .elementType(elementType)
                           // Default the damage components to common
                           .damageComponents(DamageComponents.ofCommon(), DamageSource.Strategy.REPLACE);
    }
    
    public @NotNull KnockbackSource createKnockbackCause(@NotNull HariantEntity attacker) {
        return KnockbackSource.create(attacker, HariantConstants.MELEE_KNOCKBACK_STRENGTH);
    }
    
    public @NotNull Component formatAttackSpeed() {
        return attackCooldown == 0 ? COMPONENT_NONE : Component.text("%.1f/s".formatted((double) 20 / attackCooldown));
    }
    
    public static @NotNull NormalAttack melee(@NotNull ElementType elementType, @NotNull AttributeType attributeType, double attributeScaling, int attackCooldown) {
        return new NormalAttack(elementType, attributeType, attributeScaling, attackCooldown);
    }
    
    public static @NotNull NormalAttackRanged ranged(@NotNull ElementType elementType, @NotNull AttributeType attributeType, double attributeScaling, int shotCooldown) {
        return new NormalAttackRanged(elementType, attributeType, attributeScaling, shotCooldown);
    }
    
    public static @NotNull @Singleton NormalAttack common() {
        return COMMON;
    }
    
}
