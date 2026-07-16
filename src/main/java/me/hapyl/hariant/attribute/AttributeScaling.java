package me.hapyl.hariant.attribute;

import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.util.ComponentFormatter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface AttributeScaling extends ComponentFormatter {
    
    double getScaledValue(@NotNull Attributes attributes);
    
    default double getScaledValue(@NotNull Attributable attributable) {
        return this.getScaledValue(attributable.getAttributes());
    }
    
    @Override
    @NotNull Component format();
    
    static @NotNull AttributeScaling create(@NotNull AttributeType attributeType, final double scaling, final double flat) {
        return new AttributeScalingSingle(attributeType, scaling, flat);
    }
    
    static @NotNull AttributeScaling create(@NotNull AttributeType attributeType, final double scaling) {
        return new AttributeScalingSingle(attributeType, scaling);
    }
    
    static @NotNull AttributeScaling create(@NotNull Map<? extends AttributeType, ? extends Double> scalings, final double flat) {
        return new AttributeScalingMultiple(scalings, flat);
    }
    
    static @NotNull AttributeScaling create(@NotNull Map<? extends AttributeType, ? extends Double> scalings) {
        return new AttributeScalingMultiple(scalings);
    }
    
}
