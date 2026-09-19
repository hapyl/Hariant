package me.hapyl.hariant.event;

import me.hapyl.hariant.element.ElementalAnomalySource;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyInstance;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractHariantAnomalyEvent extends HariantEntityEvent {
    
    private final ElementalAnomalyInstance anomalyInstance;
    
    AbstractHariantAnomalyEvent(@NotNull HariantEntity entity, @NotNull ElementalAnomalyInstance anomalyInstance) {
        super(entity);
        
        this.anomalyInstance = anomalyInstance;
    }
    
    public @NotNull ElementalAnomalyInstance getAnomalyInstance() {
        return anomalyInstance;
    }
    
    public @NotNull ElementalAnomalySource getAnomalySource() {
        return anomalyInstance.getAnomalySource();
    }
    
    public @NotNull ElementalAnomalyType getElementalAnomaly() {
        return anomalyInstance.getElementalAnomaly();
    }
    
    public @Nullable HariantEntity getSource() {
        return anomalyInstance.getSource();
    }
    
}
