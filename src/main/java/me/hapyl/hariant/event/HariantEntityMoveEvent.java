package me.hapyl.hariant.event;

import io.papermc.paper.event.entity.EntityMoveEvent;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.entity.HariantEntity;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class HariantEntityMoveEvent extends HariantEntityEvent implements Cancellable {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final Location from;
    private final Location to;
    
    private boolean cancel;
    
    HariantEntityMoveEvent(@NotNull HariantEntity entity, @NotNull Location from, @NotNull Location to) {
        super(entity);
        
        this.from = from;
        this.to = to;
    }
    
    @NotNull
    public Location getFrom() {
        return from;
    }
    
    @NotNull
    public Location getTo() {
        return to;
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
    
    public boolean hasChangedPosition() {
        return this.from.getX() != this.to.getX() || this.from.getY() != this.to.getY() || this.from.getZ() != this.to.getZ();
    }
    
    public boolean hasChangedBlock() {
        return this.from.getBlockX() != this.to.getBlockX() || this.from.getBlockY() != this.to.getBlockY() || this.from.getBlockZ() != this.to.getBlockZ();
    }
    
    public boolean hasChangedOrientation() {
        return this.from.getPitch() != this.to.getPitch() || this.from.getYaw() != this.to.getYaw();
    }
    
    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
    public static class Handler implements Listener {
        
        @EventHandler
        public void handlePlayerMoveEvent(PlayerMoveEvent ev) {
            callEvent(ev.getPlayer(), ev.getFrom(), ev.getTo(), ev);
        }
        
        @EventHandler
        public void handleEntityMoveEvent(EntityMoveEvent ev) {
            callEvent(ev.getEntity(), ev.getFrom(), ev.getTo(), ev);
        }
        
        private static <E extends Event & org.bukkit.event.Cancellable> void callEvent(@NotNull Entity bukkitEntity, @NotNull Location from, @NotNull Location to, @NotNull E ev) {
            Hariant.getEntity(bukkitEntity).ifPresent(entity -> {
                final HariantEntityMoveEvent event = new HariantEntityMoveEvent(entity, from, to);
                
                if (event.callEvent()) {
                    ev.setCancelled(true);
                }
            });
        }
        
    }
    
}
