package me.hapyl.hariant.event;

import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.KnockbackSource;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class HariantKnockbackEvent extends HariantEntityEvent implements Cancellable {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final KnockbackSource source;
    private final Vector velocity;
    private boolean cancel;
    
    public HariantKnockbackEvent(@NotNull HariantEntity entity, @NotNull KnockbackSource source, @NotNull Vector velocity) {
        super(entity);
        this.source = source;
        this.velocity = velocity;
    }
    
    public @NotNull KnockbackSource getSource() {
        return source;
    }
    
    public @NotNull Vector getVelocity() {
        return velocity;
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
