package me.hapyl.hariant.inventory.item.artifact.set.modifier;

import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.util.decimal.Decimal;
import me.hapyl.hariant.util.decimal.DecimalFormat;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class CommonArtifactSetModifiers {
    
    public static final ArtifactSetModifier MAX_HEALTH;
    public static final ArtifactSetModifier ATTACK;
    public static final ArtifactSetModifier DEFENSE;
    
    public static final ArtifactSetModifier CRIT_CHANCE;
    public static final ArtifactSetModifier CRIT_DAMAGE;
    public static final ArtifactSetModifier ENERGY_RECHARGE;
    public static final ArtifactSetModifier ELEMENTAL_MASTERY;
    public static final ArtifactSetModifier LUCK;
    public static final ArtifactSetModifier COOLDOWN_REDUCTION;
    
    public static final ArtifactSetModifier PHYSICAL_DAMAGE_BONUS;
    public static final ArtifactSetModifier FIRE_DAMAGE_BONUS;
    public static final ArtifactSetModifier WATER_DAMAGE_BONUS;
    public static final ArtifactSetModifier ICE_DAMAGE_BONUS;
    public static final ArtifactSetModifier TOXIC_DAMAGE_BONUS;
    public static final ArtifactSetModifier ELECTRIC_DAMAGE_BONUS;
    public static final ArtifactSetModifier AETHER_DAMAGE_BONUS;
    
    private static final Decimal ELEMENTAL_DAMAGE_BONUS_VALUE;
    
    static {
        // Attribute doesn't matter since they all use the same formatter
        ELEMENTAL_DAMAGE_BONUS_VALUE = Decimal.ofAttribute(AttributeType.PHYSICAL_DAMAGE_BONUS, 20);
        
        MAX_HEALTH = createBase(AttributeType.MAX_HEALTH, 10);
        ATTACK = createBase(AttributeType.ATTACK, 15);
        DEFENSE = createBase(AttributeType.DEFENSE, 20);
        
        CRIT_CHANCE = createAdvanced(AttributeType.CRIT_CHANCE, 20);
        CRIT_DAMAGE = createAdvanced(AttributeType.CRIT_DAMAGE, 40);
        ENERGY_RECHARGE = createAdvanced(AttributeType.ENERGY_RECHARGE, 30);
        ELEMENTAL_MASTERY = createAdvanced(AttributeType.ELEMENTAL_MASTERY, 120);
        LUCK = createAdvanced(AttributeType.LUCK, 40);
        COOLDOWN_REDUCTION = createAdvanced(AttributeType.COOLDOWN_REDUCTION, 20);
        
        PHYSICAL_DAMAGE_BONUS = createDamageBonus(ElementType.PHYSICAL);
        FIRE_DAMAGE_BONUS = createDamageBonus(ElementType.FIRE);
        WATER_DAMAGE_BONUS = createDamageBonus(ElementType.WATER);
        ICE_DAMAGE_BONUS = createDamageBonus(ElementType.ICE);
        TOXIC_DAMAGE_BONUS = createDamageBonus(ElementType.TOXIC);
        ELECTRIC_DAMAGE_BONUS = createDamageBonus(ElementType.ELECTRIC);
        AETHER_DAMAGE_BONUS = createDamageBonus(ElementType.AETHER);
    }
    
    private CommonArtifactSetModifiers() {
    }
    
    private static @NotNull ArtifactSetModifier createBase(@NotNull AttributeType attributeType, final double value) {
        return new ArtifactSetModifierImpl(
                attributeType,
                // The base modifier is always multiplicative
                AttributeModifierType.MULTIPLICATIVE,
                // Divide the value by 100 since multiplicative expects a decimal instead of the whole value
                value / 100,
                createComponent(attributeType, DecimalFormat.PERCENTAGE, value)
        );
    }
    
    private static @NotNull ArtifactSetModifier createAdvanced(@NotNull AttributeType attributeType, double value) {
        return new ArtifactSetModifierImpl(
                attributeType,
                // The advanced modifier is always flat
                AttributeModifierType.FLAT,
                value,
                // We use the attribute as the decimal format
                createComponent(attributeType, attributeType, value)
        );
    }
    
    private static @NotNull ArtifactSetModifier createDamageBonus(@NotNull ElementType elementType) {
        final AttributeType offensiveAttribute = Objects.requireNonNull(elementType.getOffensiveAttribute(), "Unsupported element type: %s".formatted(elementType.name()));
        
        return new ArtifactSetModifierImpl(
                offensiveAttribute,
                AttributeModifierType.FLAT,
                ELEMENTAL_DAMAGE_BONUS_VALUE.doubleValue(),
                Component.empty()
                         .append(Component.text("Increases "))
                         .append(elementType.asComponentDamage())
                         .append(Component.text(" dealt by "))
                         .append(ELEMENTAL_DAMAGE_BONUS_VALUE)
                         .append(Component.text("."))
        );
    }
    
    private static @NotNull Component createComponent(@NotNull AttributeType attributeType, @NotNull DecimalFormat decimalFormat, double value) {
        return Component.empty()
                        .append(Component.text("Increases "))
                        .append(attributeType)
                        .append(Component.text(" by "))
                        .append(decimalFormat.format(value).color(Colors.NUMBER))
                        .append(Component.text("."));
    }
    
}
