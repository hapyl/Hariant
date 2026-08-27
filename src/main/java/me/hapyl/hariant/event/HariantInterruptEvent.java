package me.hapyl.hariant.event;

import me.hapyl.hariant.entity.damage.AssistSource;
import me.hapyl.hariant.entity.player.HariantPlayer;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HariantInterruptEvent extends HariantPlayerEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final AssistSource assistSource;
    private final int numberOfCancelledDelegates;
    
    public HariantInterruptEvent(@NotNull HariantPlayer player, @NotNull AssistSource assistSource, int numberOfCancelledDelegates) {
        super(player);
        this.assistSource = assistSource;
        this.numberOfCancelledDelegates = numberOfCancelledDelegates;
    }
    
    public @NotNull AssistSource getAssistSource() {
        return assistSource;
    }
    
    public int getNumberOfCancelledDelegates() {
        return numberOfCancelledDelegates;
    }
    
    public boolean hasCancelledDelegates() {
        return numberOfCancelledDelegates > 0;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
}
