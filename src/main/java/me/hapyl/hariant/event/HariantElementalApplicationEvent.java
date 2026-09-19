package me.hapyl.hariant.event;

import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HariantElementalApplicationEvent extends HariantEntityEvent implements Cancellable {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final ElementType elementType;
    private final @Nullable HariantEntity source;
    
    private double elementalUnits;
    
    public HariantElementalApplicationEvent(@NotNull HariantEntity entity, @NotNull ElementType elementType, double elementUnits, @Nullable HariantEntity source) {
        super(entity);
        
        this.elementType = elementType;
        this.elementalUnits = elementUnits;
        this.source = source;
    }
    
    public @NotNull ElementType getElementType() {
        return elementType;
    }
    
    public double getElementalUnits() {
        return elementalUnits;
    }
    
    public void setElementalUnits(double elementalUnits) {
        this.elementalUnits = Math.max(1, elementalUnits);
    }
    
    public @Nullable HariantEntity getSource() {
        return source;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    @Override
    public boolean isCancelled() {
        return false;
    }
    
    @Override
    public void setCancelled(boolean cancel) {
    
    }
    
    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
}
