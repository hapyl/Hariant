package me.hapyl.hariant.event;

import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.handler.HariantProjectile;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HariantProjectileHitEvent extends HariantEvent implements Cancellable {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final HariantEntity entity;
    private final Block block;
    private final HariantProjectile projectile;
    
    private boolean cancel;
    
    public HariantProjectileHitEvent(@Nullable HariantEntity entity, @Nullable Block block, @NotNull HariantProjectile projectile) {
        this.entity = entity;
        this.block = block;
        this.projectile = projectile;
    }
    
    @Nullable
    public HariantEntity getEntity() {
        return entity;
    }
    
    @Nullable
    public Block getBlock() {
        return block;
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
    
    /**
     * Cancels this event, which will result in the projectile not triggering block-interactions and will not damage hit entity.
     *
     * <p>
     * The projectile will be removed regardless.
     * </p>
     *
     * @param cancel - {@code true} to cancel the event; {@code false} to pass.
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
    
    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
}
