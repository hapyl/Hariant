package me.hapyl.hariant.event;

import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;

public class HariantAttributeEvent extends HariantEffectEvent {
    
    private final AttributeModifier attributeModifier;
    
    public HariantAttributeEvent(@NotNull HariantEntity entity, @NotNull AttributeModifier attributeModifier) {
        super(entity, attributeModifier.getApplier(), attributeModifier.getEffectType());
        
        this.attributeModifier = attributeModifier;
    }
    
    @NotNull
    public AttributeModifier getAttributeModifier() {
        return attributeModifier;
    }
    
}
