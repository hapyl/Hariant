package me.hapyl.hariant.event;

import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.heal.HealingSource;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public class HariantHealEvent extends HariantHealthChangeEvent implements Cancellable {
    
    private final HealingSource healingSource;
    private final double actualHealing;
    
    private boolean cancel;
    
    public HariantHealEvent(@NotNull HariantEntity entity, @NotNull HealingSource healingSource, double healthBeforeHealing, double healthAfterHealing, double actualHealing) {
        super(entity, healthBeforeHealing, healthAfterHealing);
        
        this.healingSource = healingSource;
        this.actualHealing = actualHealing;
    }
    
    public @NotNull HealingSource getHealingSource() {
        return healingSource;
    }
    
    public double getActualHealing() {
        return actualHealing;
    }
    
    @Override
    public boolean isCancelled() {
        return cancel;
    }
    
    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
    
}
