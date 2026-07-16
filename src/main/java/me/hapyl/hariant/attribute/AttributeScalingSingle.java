package me.hapyl.hariant.attribute;

import me.hapyl.hariant.attribute.instance.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class AttributeScalingSingle extends AttributeScalingImpl {
    
    private final AttributeType attributeType;
    private final double scaling;
    private final double flat;
    
    public AttributeScalingSingle(@NotNull AttributeType attributeType, double scalingPercent, double flat) {
        super(createFormat(Map.of(attributeType, scalingPercent), flat));
        
        this.attributeType = attributeType;
        this.scaling = scalingPercent;
        this.flat = flat;
    }
    
    public AttributeScalingSingle(@NotNull AttributeType attributeType, double scalingPercent) {
        this(attributeType, scalingPercent, 0);
    }
    
    public double getScaledValue(@NotNull Attributes attributes) {
        return attributes.get(attributeType) * (scaling / 100) + flat;
    }
    
}
