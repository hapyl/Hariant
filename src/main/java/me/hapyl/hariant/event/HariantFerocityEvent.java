package me.hapyl.hariant.event;

import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.ferocity.FerocitySource;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HariantFerocityEvent extends HariantEntityEvent implements Cancellable {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final FerocitySource ferocitySource;
    private boolean cancel;
    
    public HariantFerocityEvent(@NotNull HariantEntity entity,@NotNull FerocitySource ferocitySource) {
        super(entity);
        
        this.ferocitySource = ferocitySource;
    }
    
    public @NotNull FerocitySource getFerocitySource() {
        return ferocitySource;
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
