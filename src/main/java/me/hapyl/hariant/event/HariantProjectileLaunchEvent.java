package me.hapyl.hariant.event;

import me.hapyl.hariant.handler.HariantProjectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HariantProjectileLaunchEvent extends HariantEvent implements Cancellable {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final HariantProjectile projectile;
    private boolean cancel;
    
    public HariantProjectileLaunchEvent(@NotNull HariantProjectile projectile) {
        this.projectile = projectile;
    }
    
    @NotNull
    public HariantProjectile getProjectile() {
        return projectile;
    }
    
    @NotNull
    @Override
    public HandlerList getHandlers() {
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
    
    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
}
