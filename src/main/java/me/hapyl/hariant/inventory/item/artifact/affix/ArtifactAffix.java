package me.hapyl.hariant.inventory.item.artifact.affix;

import me.hapyl.hariant.attribute.AttributeType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public enum ArtifactAffix implements ComponentLike {
    
    MAX_HEATH(AttributeType.MAX_HEALTH, 100, 100),
    DEFENSE(AttributeType.DEFENSE, 100, 100),
    
    ATTACK(AttributeType.ATTACK, 60, 100),
    CRIT_CHANCE(AttributeType.CRIT_CHANCE, 20, 50),
    CRIT_DAMAGE(AttributeType.CRIT_DAMAGE, 40, 50),
    
    ENERGY_RECHARGE(AttributeType.ENERGY_RECHARGE, 24, 100),
    EFFECT_RESISTANCE(AttributeType.EFFECT_RESISTANCE, 16, 100),
    ELEMENTAL_MASTERY(AttributeType.ELEMENTAL_MASTERY, 60, 100),
    VITALITY(AttributeType.VITALITY, 48, 60),
    MENDING(AttributeType.MENDING, 48, 60),
    LUCK(AttributeType.LUCK, 20, 20),
    
    PHYSICAL_DAMAGE_BONUS(AttributeType.PHYSICAL_DAMAGE_BONUS, 20, 100),
    FIRE_DAMAGE_BONUS(AttributeType.FIRE_DAMAGE_BONUS, 20, 100),
    WATER_DAMAGE_BONUS(AttributeType.WATER_DAMAGE_BONUS, 20, 100),
    ICE_DAMAGE_BONUS(AttributeType.ICE_DAMAGE_BONUS, 20, 100),
    TOXIC_DAMAGE_BONUS(AttributeType.TOXIC_DAMAGE_BONUS, 20, 100),
    ELECTRIC_DAMAGE_BONUS(AttributeType.ELECTRIC_DAMAGE_BONUS, 20, 100),
    AETHER_DAMAGE_BONUS(AttributeType.AETHER_DAMAGE_BONUS, 20, 100),
    
    PHYSICAL_RESISTANCE(AttributeType.PHYSICAL_RESISTANCE, 40, 50),
    FIRE_RESISTANCE(AttributeType.FIRE_RESISTANCE, 40, 50),
    WATER_RESISTANCE(AttributeType.WATER_RESISTANCE, 40, 50),
    ICE_RESISTANCE(AttributeType.ICE_RESISTANCE, 40, 50),
    TOXIC_RESISTANCE(AttributeType.TOXIC_RESISTANCE, 40, 50),
    ELECTRIC_RESISTANCE(AttributeType.ELECTRIC_RESISTANCE, 40, 50),
    AETHER_RESISTANCE(AttributeType.AETHER_RESISTANCE, 40, 50);
    
    private final AttributeType attributeType;
    private final double value;
    private final int weight;
    
    ArtifactAffix(@NotNull AttributeType attributeType, double value, int weight) {
        this.attributeType = attributeType;
        this.value = value;
        this.weight = weight;
    }
    
    public @NotNull AttributeType getAttributeType() {
        return attributeType;
    }
    
    public double getValue() {
        return value;
    }
    
    public int getWeight() {
        return weight;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return Component.empty().append(attributeType).append(Component.text(" +")).append(attributeType.format(value));
    }
    
}