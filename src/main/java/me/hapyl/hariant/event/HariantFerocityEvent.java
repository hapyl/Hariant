package me.hapyl.hariant.event;

import me.hapyl.hariant.entity.HariantEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HariantFerocityEvent extends HariantEntityEvent implements Cancellable {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final HariantEntity source;
    private int ferocityStrikes;
    private boolean cancel;
    
    public HariantFerocityEvent(@NotNull HariantEntity entity, @Nullable HariantEntity source, int ferocityStrikes) {
        super(entity);
        
        this.source = source;
        this.ferocityStrikes = ferocityStrikes;
    }
    
    public @Nullable HariantEntity getSource() {
        return source;
    }
    
    public int getFerocityStrikes() {
        return ferocityStrikes;
    }
    
    public void setFerocityStrikes(int ferocityStrikes) {
        this.ferocityStrikes = Math.max(1, ferocityStrikes);
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
