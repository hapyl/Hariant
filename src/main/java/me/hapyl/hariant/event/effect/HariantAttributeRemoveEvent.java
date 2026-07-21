package me.hapyl.hariant.event.effect;

import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.event.HariantEntityEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HariantAttributeRemoveEvent extends HariantEntityEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final AttributeModifier attributeModifier;
    
    public HariantAttributeRemoveEvent(@NotNull HariantEntity entity, @NotNull AttributeModifier attributeModifier) {
        super(entity);
        
        this.attributeModifier = attributeModifier;
    }
    
    public @NotNull AttributeModifier getAttributeModifier() {
        return attributeModifier;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
}
