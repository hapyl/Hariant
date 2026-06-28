package me.hapyl.hariant.attribute;

import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.util.Arithmetic;
import me.hapyl.hariant.util.ComponentFormatter;
import me.hapyl.hariant.util.decimal.DecimalFormat;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class AttributeScaling implements ComponentFormatter, Arithmetic<AttributeScaling> {
    
    private final AttributeType attributeType;
    private final double scaling;
    private final Component format;
    
    protected AttributeScaling(@NotNull AttributeType attributeType, double scalingPercent) {
        this.attributeType = attributeType;
        this.scaling = scalingPercent;
        this.format = Component.empty()
                               .append(DecimalFormat.PERCENTAGE.format(scalingPercent))
                               .appendSpace()
                               .append(attributeType.abbreviation());
    }
    
    public double getScaling() {
        return scaling;
    }
    
    public double getScaledValue(@NotNull Attributes attributes) {
        return attributes.get(attributeType) * (scaling / 100);
    }
    
    public double getScaledValue(@NotNull Attributable attributable) {
        return getScaledValue(attributable.getAttributes());
    }
    
    @NotNull
    @Override
    public Component format() {
        return format;
    }
    
    @Override
    public double add(@NotNull AttributeScaling that) {
        return this.scaling - that.scaling;
    }
    
    @Override
    public double subtract(@NotNull AttributeScaling that) {
        return this.scaling - that.scaling;
    }
    
    @Override
    public double multiply(@NotNull AttributeScaling that) {
        return this.scaling * that.scaling;
    }
    
    @Override
    public double divide(@NotNull AttributeScaling that) {
        return that.scaling != 0 ? this.scaling / that.scaling : 0;
    }
    
    @NotNull
    public static AttributeScaling create(@NotNull AttributeType attributeType, final double scaling) {
        return new AttributeScaling(attributeType, scaling);
    }
    
    public static double scale(@NotNull HariantEntity entity, @NotNull AttributeType attributeType, final double scaling) {
        return create(attributeType, scaling).getScaledValue(entity);
    }
    
}
