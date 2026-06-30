package me.hapyl.hariant.event;

import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.heal.HealingSource;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HariantHealEvent extends HariantEntityEvent implements Cancellable {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final @NotNull HealingSource healingSource;
    
    private final double healthBeforeHealing;
    private final double healthAfterHealing;
    
    private final double actualHealing;
    private final double excessHealing;
    
    private boolean cancel;
    
    public HariantHealEvent(@NotNull HariantEntity entity, @NotNull HealingSource healingSource, double healthBeforeHealing, double healthAfterHealing, double actualHealing, double excessHealing) {
        super(entity);
        
        this.healingSource = healingSource;
        this.healthBeforeHealing = healthBeforeHealing;
        this.healthAfterHealing = healthAfterHealing;
        this.actualHealing = actualHealing;
        this.excessHealing = excessHealing;
    }
    
    public @NotNull HealingSource getHealingSource() {
        return healingSource;
    }
    
    public double getHealthBeforeHealing() {
        return healthBeforeHealing;
    }
    
    public double getHealthAfterHealing() {
        return healthAfterHealing;
    }
    
    public double getActualHealing() {
        return actualHealing;
    }
    
    public double getExcessHealing() {
        return excessHealing;
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
