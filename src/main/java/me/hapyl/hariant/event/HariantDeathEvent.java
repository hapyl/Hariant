package me.hapyl.hariant.event;

import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageInstance;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HariantDeathEvent extends HariantEntityEvent implements Cancellable {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final DamageInstance damageInstance;
    private boolean cancel;
    
    public HariantDeathEvent(@NotNull HariantEntity entity, @NotNull DamageInstance damageInstance) {
        super(entity);
        
        this.damageInstance = damageInstance;
    }
    
    @NotNull
    public DamageInstance getDamageInstance() {
        return damageInstance;
    }
    
    @Override
    public boolean isCancelled() {
        return cancel;
    }
    
    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
    
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
}

