package me.hapyl.hariant.event;

import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import me.hapyl.hariant.entity.HariantEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HariantElementalAnomalyEvent extends HariantEntityEvent implements Cancellable {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final ElementalAnomalyType elementalAnomaly;
    private final HariantEntity source;
    
    private boolean cancel;
    
    public HariantElementalAnomalyEvent(@NotNull HariantEntity entity, @NotNull ElementalAnomalyType elementalAnomaly, @Nullable HariantEntity source) {
        super(entity);
        
        this.elementalAnomaly = elementalAnomaly;
        this.source = source;
    }
    
    @NotNull
    public ElementalAnomalyType getElementalAnomaly() {
        return elementalAnomaly;
    }
    
    @Nullable
    public HariantEntity getSource() {
        return source;
    }
    
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    @Override
    public boolean isCancelled() {
        return this.cancel;
    }
    
    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
    
    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
}
