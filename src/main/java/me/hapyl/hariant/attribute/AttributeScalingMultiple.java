package me.hapyl.hariant.attribute;

import me.hapyl.hariant.attribute.instance.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class AttributeScalingMultiple extends AttributeScalingImpl {
    
    private final Map<? extends AttributeType, ? extends Double> scalingMap;
    private final double flat;
    
    public AttributeScalingMultiple(@NotNull Map<? extends AttributeType, ? extends Double> scalingMap, double flat) {
        super(createFormat(scalingMap, flat));
        
        this.scalingMap = scalingMap;
        this.flat = flat;
    }
    
    public AttributeScalingMultiple(@NotNull Map<? extends AttributeType, ? extends Double> scalingMap) {
        this(scalingMap, 0);
    }
    
    @Override
    public double getScaledValue(@NotNull Attributes attributes) {
        double value = 0;
        
        for (Map.Entry<? extends AttributeType, ? extends Double> entry : scalingMap.entrySet()) {
            value += attributes.get(entry.getKey()) * (entry.getValue() / 100);
        }
        
        return value + flat;
    }
    
}
