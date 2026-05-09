package me.hapyl.hariant.attribute;

import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.util.ComponentFormatter;
import me.hapyl.hariant.util.decimal.DecimalFormat;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class AttributeScaling implements ComponentFormatter {
    
    protected final AttributeType attributeType;
    protected final double scaling;
    
    private final Component format;
    
    protected AttributeScaling(@NotNull AttributeType attributeType, double scalingPercent) {
        if (!attributeType.isBase()) {
            throw new IllegalArgumentException("Cannot scale of non-base attribute!");
        }
        
        this.attributeType = attributeType;
        this.scaling = scalingPercent;
        this.format = Component.empty()
                               .append(DecimalFormat.PERCENTAGE.format(scalingPercent))
                               .append(Component.text(" "))
                               .append(attributeType.abbreviation());
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
    
    @NotNull
    public static AttributeScaling of(@NotNull AttributeType attributeType, final double scaling) {
        return new AttributeScaling(attributeType, scaling);
    }
    
}
