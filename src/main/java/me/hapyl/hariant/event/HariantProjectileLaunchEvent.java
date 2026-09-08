package me.hapyl.hariant.event;

import me.hapyl.hariant.handler.HariantProjectile;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HariantProjectileLaunchEvent extends HariantEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final HariantProjectile projectile;
    private boolean cancel;
    
    public HariantProjectileLaunchEvent(@NotNull HariantProjectile projectile) {
        this.projectile = projectile;
    }
    
    public @NotNull HariantProjectile getProjectile() {
        return projectile;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
}
