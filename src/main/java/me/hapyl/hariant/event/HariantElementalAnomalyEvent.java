package me.hapyl.hariant.event;

import me.hapyl.hariant.element.anomaly.ElementalAnomalyInstance;
import me.hapyl.hariant.entity.HariantEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HariantElementalAnomalyEvent extends AbstractHariantAnomalyEvent implements Cancellable {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private boolean cancel;
    
    public HariantElementalAnomalyEvent(@NotNull HariantEntity entity, @NotNull ElementalAnomalyInstance instance) {
        super(entity, instance);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
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
