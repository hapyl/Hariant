package me.hapyl.hariant.event;

import me.hapyl.hariant.element.ElementalAnomalySource;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import me.hapyl.hariant.entity.HariantEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HariantElementalAnomalyEvent extends HariantEntityEvent implements Cancellable {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final ElementalAnomalySource source;
    
    private boolean cancel;
    
    public HariantElementalAnomalyEvent(@NotNull HariantEntity entity, @NotNull ElementalAnomalySource source) {
        super(entity);
        
        this.source = source;
    }
    
    public @NotNull ElementalAnomalySource getAnomalySource() {
        return source;
    }
    
    public @NotNull ElementalAnomalyType getElementalAnomaly() {
        return source.getElementalAnomaly();
    }
    
    public @Nullable HariantEntity getSource() {
        return source.getSource();
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
