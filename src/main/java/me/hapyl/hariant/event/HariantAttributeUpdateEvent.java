package me.hapyl.hariant.event;

import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.entity.HariantEntity;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HariantAttributeUpdateEvent extends HariantEntityEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final AttributeType attributeType;
    private final double value;
    
    public HariantAttributeUpdateEvent(@NotNull HariantEntity entity, @NotNull AttributeType attributeType, double value) {
        super(entity);
        
        this.attributeType = attributeType;
        this.value = value;
    }
    
    public @NotNull AttributeType getAttributeType() {
        return attributeType;
    }
    
    public double getValue() {
        return value;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
}
