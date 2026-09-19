package me.hapyl.hariant.event;

import me.hapyl.hariant.element.anomaly.ElementalAnomalyInstance;
import me.hapyl.hariant.entity.HariantEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HariantElementalAnomalyQueueEvent extends AbstractHariantAnomalyEvent implements Cancellable {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private boolean cancel;
    
    public HariantElementalAnomalyQueueEvent(@NotNull HariantEntity entity, @NotNull ElementalAnomalyInstance anomalyInstance) {
        super(entity, anomalyInstance);
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    @Override
    public boolean isCancelled() {
        return cancel;
    }
    
    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
    
    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
}
