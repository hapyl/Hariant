package me.hapyl.hariant.attribute;

import me.hapyl.hariant.Colors;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.util.decimal.DecimalFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.attribute.AttributeInstance;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public enum AttributeType implements Attribute {
    
    MAX_HEALTH(
            new AttributeImpl(
                    Component.text("❤"),
                    Component.text("Max Health"),
                    Component.text("The maximum health of the player."),
                    Colors.ATTRIBUTE_MAX_HEALTH,
                    DecimalFormat.FLAT
            ) {
                @Override
                public double defaultValue() {
                    return 1_000;
                }
                
                @Override
                public double maxValue() {
                    return 1_000_000;
                }
                
                @NotNull
                @Override
                public Component abbreviation() {
                    return Component.text("HP");
                }
                
                @Override
                public boolean isBase() {
                    return true;
                }
                
                @Override
                public void update(@NotNull HariantEntity entity, double newValue) {
                    entity.updateHealth(newValue);
                }
            }
    ),
    
    ATTACK(
            new AttributeImpl(
                    Component.text("⚔"),
                    Component.text("Attack"),
                    Component.text("The attack power of the player."),
                    Colors.ATTRIBUTE_ATTACK,
                    DecimalFormat.FLAT
            ) {
                @Override
                public double defaultValue() {
                    return 100;
                }
                
                @Override
                public double maxValue() {
                    return 10_000;
                }
                
                @NotNull
                @Override
                public Component abbreviation() {
                    return Component.text("ATK");
                }
                
                @Override
                public boolean isBase() {
                    return true;
                }
            }
    ),
    
    DEFENSE(
            new AttributeImpl(
                    Component.text("🛡"),
                    Component.text("Defense"),
                    Component.text("The defense of the player."),
                    Colors.ATTRIBUTE_DEFENSE,
                    DecimalFormat.FLAT
            ) {
                
                @Override
                public double minValue() {
                    return -80;
                }
                
                @Override
                public double defaultValue() {
                    return 100;
                }
                
                @Override
                public double maxValue() {
                    return 1_000;
                }
                
                @NotNull
                @Override
                public Component abbreviation() {
                    return Component.text("DEF");
                }
                
                @Override
                public boolean isBase() {
                    return true;
                }
            }
    ),
    
    CRIT_CHANCE(
            new AttributeImpl(
                    Component.text("☣"),
                    Component.text("Crit Chance"),
                    Component.text("The chance for an attack to deal critical damage."),
                    Colors.ATTRIBUTE_CRIT_CHANCE,
                    DecimalFormat.PERCENTAGE
            ) {
                @Override
                public double defaultValue() {
                    return 5;
                }
                
                @Override
                public double minValue() {
                    return -100; // For funsies
                }
                
                @Override
                public double maxValue() {
                    return 1_000;
                }
                
                @NotNull
                @Override
                public Component abbreviation() {
                    return Component.text("CC");
                }
            }
    ),
    
    CRIT_DAMAGE(
            new AttributeImpl(
                    Component.text("☠"),
                    Component.text("Crit Damage"),
                    Component.text("The critical multiplier for an attack that scores a critical hit."),
                    Colors.ATTRIBUTE_CRIT_DAMAGE,
                    DecimalFormat.PERCENTAGE
            ) {
                @Override
                public double defaultValue() {
                    return 50;
                }
                
                @NotNull
                @Override
                public Component abbreviation() {
                    return Component.text("CD");
                }
            }
    ),
    
    MOVEMENT_SPEED(
            new AttributeImpl(
                    Component.text("\uD83D\uDC3E"),
                    Component.text("Movement Speed"),
                    Component.text("The movement speed of the player."),
                    Colors.ATTRIBUTE_MOVEMENT_SPEED,
                    DecimalFormat.PERCENTAGE
            ) {
                private static final double SCALE = 1000;
                
                @Override
                public double defaultValue() {
                    return 100;
                }
                
                @Override
                public double minValue() {
                    return -100;
                }
                
                @Override
                public double maxValue() {
                    return 500;
                }
                
                @NotNull
                @Override
                public Component abbreviation() {
                    return Component.text("SPD");
                }
                
                @Override
                public void update(@NotNull HariantEntity entity, double newValue) {
                    final AttributeInstance vanillaAttribute = entity.getVanillaAttribute(org.bukkit.attribute.Attribute.MOVEMENT_SPEED);
                    
                    vanillaAttribute.setBaseValue(newValue / SCALE);
                }
            }
    ),
    
    ENERGY_RECHARGE(
            new AttributeImpl(
                    Component.text("🔃"),
                    Component.text("Energy Recharge"),
                    Component.text("The multiplier for how fast energy regenerates."),
                    Colors.ATTRIBUTE_ENERGY_RECHARGE,
                    DecimalFormat.PERCENTAGE
            ) {
                @Override
                public double defaultValue() {
                    return 100;
                }
                
                @Override
                public double maxValue() {
                    return 500;
                }
                
                @NotNull
                @Override
                public Component abbreviation() {
                    return Component.text("ER");
                }
            }
    ),
    
    EFFECT_RESISTANCE(
            new AttributeImpl(
                    Component.text("🐚"),
                    Component.text("Effect Resistance"),
                    Component.text("The chance to resist negative effects."),
                    Colors.ATTRIBUTE_EFFECT_RESISTANCE,
                    DecimalFormat.PERCENTAGE
            )
    ),
    
    ELEMENTAL_MASTERY(
            new AttributeImpl(
                    Component.text("❇"),
                    Component.text("Elemental Mastery"),
                    Component.text("Increases the element build-up rate and anomaly duration."),
                    Colors.ATTRIBUTE_ELEMENTAL_MASTERY,
                    DecimalFormat.DECIMAL
            ) {
                @Override
                public double maxValue() {
                    return 2_000;
                }
            }
    ),
    
    KNOCKBACK_RESISTANCE(
            new AttributeImpl(
                    Component.text("⚓"),
                    Component.text("Knockback Resistance"),
                    Component.text("The percentage of knockback resisted."),
                    Colors.ATTRIBUTE_KNOCKBACK_RESISTANCE,
                    DecimalFormat.PERCENTAGE
            )
    ),
    
    VITALITY(
            new AttributeImpl(
                    Component.text("\uD83E\uDE78"),
                    Component.text("Vitality"),
                    Component.text("The incoming healing multiplier."),
                    Colors.ATTRIBUTE_VITALITY,
                    DecimalFormat.PERCENTAGE
            ) {
                @Override
                public double defaultValue() {
                    return 100;
                }
                
                @Override
                public double maxValue() {
                    return 500;
                }
            }
    ),
    
    MENDING(
            new AttributeImpl(
                    Component.text("🌿"),
                    Component.text("Mending"),
                    Component.text("The outgoing healing multiplier."),
                    Colors.ATTRIBUTE_MENDING,
                    DecimalFormat.PERCENTAGE
            ) {
                @Override
                public double defaultValue() {
                    return 100;
                }
                
                @Override
                public double maxValue() {
                    return 500;
                }
            }
    ),
    
    // *-* Elemental Damage Bonus *-* //
    
    PHYSICAL_DAMAGE_BONUS(AttributeElementalImpl.ofElementalDamageBonus(ElementType.PHYSICAL)),
    FIRE_DAMAGE_BONUS(AttributeElementalImpl.ofElementalDamageBonus(ElementType.FIRE)),
    WATER_DAMAGE_BONUS(AttributeElementalImpl.ofElementalDamageBonus(ElementType.WATER)),
    ICE_DAMAGE_BONUS(AttributeElementalImpl.ofElementalDamageBonus(ElementType.ICE)),
    TOXIC_DAMAGE_BONUS(AttributeElementalImpl.ofElementalDamageBonus(ElementType.TOXIC)),
    ELECTRIC_DAMAGE_BONUS(AttributeElementalImpl.ofElementalDamageBonus(ElementType.ELECTRIC)),
    AETHER_DAMAGE_BONUS(AttributeElementalImpl.ofElementalDamageBonus(ElementType.AETHER)),
    
    // *-* Elemental Resistance *-* //
    
    PHYSICAL_RESISTANCE(AttributeElementalImpl.ofElementalResistance(ElementType.PHYSICAL)),
    FIRE_RESISTANCE(AttributeElementalImpl.ofElementalResistance(ElementType.FIRE)),
    WATER_RESISTANCE(AttributeElementalImpl.ofElementalResistance(ElementType.WATER)),
    ICE_RESISTANCE(AttributeElementalImpl.ofElementalResistance(ElementType.ICE)),
    TOXIC_RESISTANCE(AttributeElementalImpl.ofElementalResistance(ElementType.TOXIC)),
    ELECTRIC_RESISTANCE(AttributeElementalImpl.ofElementalResistance(ElementType.ELECTRIC)),
    AETHER_RESISTANCE(AttributeElementalImpl.ofElementalResistance(ElementType.AETHER));
    
    
    private static final List<AttributeType> BASE_ATTRIBUTES;
    private static final List<AttributeType> ADVANCED_ATTRIBUTES;
    private static final List<AttributeType> ELEMENTAL_DAMAGE_BONUSES;
    private static final List<AttributeType> ELEMENTAL_RESISTANCE;
    
    private static final Map<ElementType, AttributeType[]> ELEMENTAL_ATTRIBUTES;
    
    static {
        BASE_ATTRIBUTES = Arrays.stream(AttributeType.values()).filter(AttributeType::isBase).toList();
        ADVANCED_ATTRIBUTES = Arrays.stream(AttributeType.values())
                                    .filter(attributeType -> !attributeType.isBase() && !(attributeType.attribute instanceof AttributeElementalImpl))
                                    .toList();
        
        ELEMENTAL_DAMAGE_BONUSES = List.of(PHYSICAL_DAMAGE_BONUS, FIRE_DAMAGE_BONUS, WATER_DAMAGE_BONUS, ICE_DAMAGE_BONUS, TOXIC_DAMAGE_BONUS, ELECTRIC_DAMAGE_BONUS, AETHER_DAMAGE_BONUS);
        ELEMENTAL_RESISTANCE = List.of(PHYSICAL_RESISTANCE, FIRE_RESISTANCE, WATER_RESISTANCE, ICE_RESISTANCE, TOXIC_RESISTANCE, ELECTRIC_RESISTANCE, AETHER_RESISTANCE);
        
        ELEMENTAL_ATTRIBUTES = Map.of(
                ElementType.PHYSICAL, new AttributeType[] { AttributeType.PHYSICAL_DAMAGE_BONUS, AttributeType.PHYSICAL_RESISTANCE },
                ElementType.FIRE, new AttributeType[] { AttributeType.FIRE_DAMAGE_BONUS, AttributeType.FIRE_RESISTANCE },
                ElementType.WATER, new AttributeType[] { AttributeType.WATER_DAMAGE_BONUS, AttributeType.WATER_RESISTANCE },
                ElementType.ICE, new AttributeType[] { AttributeType.ICE_DAMAGE_BONUS, AttributeType.ICE_RESISTANCE },
                ElementType.TOXIC, new AttributeType[] { AttributeType.TOXIC_DAMAGE_BONUS, AttributeType.TOXIC_RESISTANCE },
                ElementType.ELECTRIC, new AttributeType[] { AttributeType.ELECTRIC_DAMAGE_BONUS, AttributeType.ELECTRIC_RESISTANCE },
                ElementType.AETHER, new AttributeType[] { AttributeType.AETHER_DAMAGE_BONUS, AttributeType.AETHER_RESISTANCE }
        );
    }
    
    private final Attribute attribute;
    
    AttributeType(@NotNull Attribute attribute) {
        this.attribute = attribute;
    }
    
    @Override
    public double defaultValue() {
        return attribute.defaultValue();
    }
    
    @Override
    public double minValue() {
        return attribute.minValue();
    }
    
    @Override
    public double maxValue() {
        return attribute.maxValue();
    }
    
    @Override
    public boolean isBase() {
        return attribute.isBase();
    }
    
    @Override
    public void update(@NotNull HariantEntity entity, double newValue) {
        attribute.update(entity, newValue);
    }
    
    @Override
    @NotNull
    public Component getPrefix() {
        return attribute.getPrefix();
    }
    
    @NotNull
    @Override
    public Component getName() {
        return attribute.getName();
    }
    
    @NotNull
    @Override
    public Component getDescription() {
        return attribute.getDescription();
    }
    
    @NotNull
    @Override
    public Style getStyle() {
        return attribute.getStyle();
    }
    
    @NotNull
    @Override
    public Component abbreviation() {
        return attribute.abbreviation();
    }
    
    @NotNull
    @Override
    public Component format(double value) {
        return attribute.format(value);
    }
    
    @NotNull
    public static AttributeType getElementalDamageBonusAttribute(@NotNull ElementType elementType) {
        return getElementalAttribute(elementType, true);
    }
    
    @NotNull
    public static AttributeType getElementalResistanceAttribute(@NotNull ElementType elementType) {
        return getElementalAttribute(elementType, false);
    }
    
    @NotNull
    public static List<AttributeType> getBaseAttributes() {
        return BASE_ATTRIBUTES;
    }
    
    @NotNull
    public static List<AttributeType> getAdvancedAttributes() {
        return ADVANCED_ATTRIBUTES;
    }
    
    @NotNull
    public static List<AttributeType> getElementalDamageBonuses() {
        return ELEMENTAL_DAMAGE_BONUSES;
    }
    
    @NotNull
    public static List<AttributeType> getElementalResistances() {
        return ELEMENTAL_RESISTANCE;
    }
    
    @NotNull
    private static AttributeType getElementalAttribute(@NotNull ElementType elementType, boolean damageBonus) {
        return Objects.requireNonNull(ELEMENTAL_ATTRIBUTES.get(elementType), "Illegal element: %s".formatted(elementType))[damageBonus ? 0 : 1];
    }
}
