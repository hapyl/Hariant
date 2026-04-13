package me.hapyl.hariant.attribute.instance;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.annotate.NotEmpty;
import me.hapyl.hariant.attribute.AttributeType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;

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
    
    @NotNull
    public Attributes adjust(@NotNull AttributeType attributeType, double value) {
        this.set(attributeType, value);
        return this;
    }
    
    @NotNull
    public Component getRating(@NotNull AttributeType attributeType) {
        class Holder {
            private static final int MID_RATING = 3;
            private static final int MAX_RATING = 6;
        }
        
        final double defaultValue = attributeType.defaultValue();
        final double value = this.base(attributeType); // Force base call
        
        final double ratio = value / defaultValue;
        final double quality = Math.clamp(Math.round(ratio * Holder.MID_RATING), 0, Holder.MAX_RATING);
        
        final TextComponent.Builder builder = Component.text();
        
        final Component prefix = attributeType.getPrefixStyled();
        final Component prefixGrayed = attributeType.getPrefix().color(NamedTextColor.DARK_GRAY);
        
        for (int i = 0; i < Holder.MAX_RATING; i++) {
            builder.append(i < quality ? prefix : prefixGrayed);
        }
        
        return builder.build();
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
    
    
}
