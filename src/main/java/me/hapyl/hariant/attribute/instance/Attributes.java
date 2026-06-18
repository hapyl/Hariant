package me.hapyl.hariant.attribute.instance;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.annotate.NotEmpty;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public class Attributes implements AttributesBase {
    
    @NotEmpty
    protected final EnumMap<AttributeType, Double> attributeMap;
    
    public Attributes(@Nullable Attributes copyFrom) {
        this.attributeMap = Maps.newEnumMap(AttributeType.class);
        
        for (AttributeType attributeType : AttributeType.values()) {
            // Either supply the map with the attribute value from `copyFrom` or with a default attribute value
            this.attributeMap.put(attributeType, copyFrom != null ? copyFrom.get(attributeType) : attributeType.defaultValue());
        }
    }
    
    @Override
    public double get(@NotNull AttributeType attributeType) {
        return attributeType.clamp(attributeMap.get(attributeType));
    }
    
    @Override
    public final double base(@NotNull AttributeType attributeType) {
        return attributeType.clamp(attributeMap.get(attributeType));
    }
    
    @Override
    public void set(@NotNull AttributeType attributeType, final double value) {
        this.attributeMap.put(attributeType, value);
    }
    
    @Override
    public void add(@NotNull AttributeType attributeType, double value) {
        attributeMap.merge(attributeType, value, Double::sum);
    }
    
    @NotNull
    public Attributes adjust(@NotNull AttributeType attributeType, double value) {
        this.set(attributeType, value);
        return this;
    }
    
    @NotNull
    public Component createLore(@NotNull AttributeType attributeType) {
        return Component.empty()
                        .appendSpace()
                        .append(attributeType.asComponent())
                        .appendSpace()
                        .append(attributeType.format(this.get(attributeType)));
    }
    
    @NotNull
    public Component createRelativeArrow(@NotNull AttributeType attributeType) {
        final double baseValue = this.base(attributeType);
        final double defaultValue = attributeType.defaultValue();
        
        return baseValue > defaultValue
               ? Component.text("▲", Colors.GREEN)
               : baseValue < defaultValue
                 ? Component.text("▼", Colors.RED)
                 : Component.text("■", Colors.DARK_GRAY);
    }
    
    @NotNull
    public static Attributes base(final double maxHealth, final double attack, final double defense) {
        final Attributes attributes = new Attributes(null);
        attributes.set(AttributeType.MAX_HEALTH, maxHealth);
        attributes.set(AttributeType.ATTACK, attack);
        attributes.set(AttributeType.DEFENSE, defense);
        
        return attributes;
    }
    
    @NotNull
    public static Attributes zero() {
        final Attributes attributes = new Attributes(null);
        attributes.attributeMap.replaceAll((k, v) -> 0.0);
        
        return attributes;
    }
    
    @NotNull
    public static Attributes common() {
        return base(1000, 100, 100);
    }
    
    @NotNull
    public static Attributes copyOf(@NotNull Attributes attributes) {
        return new Attributes(attributes);
    }
    
}
