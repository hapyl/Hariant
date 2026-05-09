package me.hapyl.hariant.event;

import me.hapyl.hariant.entity.player.HariantPlayer;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HariantPlayerCreateEvent extends HariantPlayerEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    public HariantPlayerCreateEvent(@NotNull HariantPlayer player) {
        super(player);
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
