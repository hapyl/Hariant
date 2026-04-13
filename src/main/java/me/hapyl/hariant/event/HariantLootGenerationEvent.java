package me.hapyl.hariant.event;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HariantLootGenerationEvent extends HariantEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private int rollAmount;
    
    public HariantLootGenerationEvent(int rollAmount) {
        this.rollAmount = rollAmount;
    }
    
    public int getRollAmount() {
        return rollAmount;
    }
    
    public void setRollAmount(final int rollAmount) {
        this.rollAmount = Math.max(1, rollAmount);
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
