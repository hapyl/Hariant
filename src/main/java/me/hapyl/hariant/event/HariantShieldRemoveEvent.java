package me.hapyl.hariant.event;

import me.hapyl.hariant.entity.shield.Shield;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HariantShieldRemoveEvent extends HariantShieldEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final Shield.Cause cause;
    
    public HariantShieldRemoveEvent(@NotNull Shield shield, @NotNull Shield.Cause cause) {
        super(shield);
        
        this.cause = cause;
    }
    
    public @NotNull Shield.Cause getCause() {
        return cause;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
