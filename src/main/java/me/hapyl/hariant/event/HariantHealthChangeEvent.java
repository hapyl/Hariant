package me.hapyl.hariant.event;

import me.hapyl.hariant.entity.HariantEntity;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HariantHealthChangeEvent extends HariantEntityEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final double previousHealth;
    private final double newHealth;
    
    public HariantHealthChangeEvent(@NotNull HariantEntity entity, double previousHealth, double newHealth) {
        super(entity);
        this.previousHealth = previousHealth;
        this.newHealth = newHealth;
    }
    
    public double getPreviousHealth() {
        return previousHealth;
    }
    
    public double getNewHealth() {
        return newHealth;
    }
    
    public double getHealthDifference() {
        return Math.max(previousHealth, newHealth) - Math.min(previousHealth, newHealth);
    }
    
    public boolean isHealing() {
        return newHealth > previousHealth;
    }
    
    public boolean isDamage() {
        return newHealth < previousHealth;
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
