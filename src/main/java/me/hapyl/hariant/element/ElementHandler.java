package me.hapyl.hariant.element;

import me.hapyl.hariant.element.anomaly.ElementalAnomaly;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElementHandler {
    
    boolean applyElement(@NotNull ElementSource elementSource);
    
    boolean triggerAnomaly(@NotNull ElementalAnomalySource anomalySource, boolean force);
    
    double getElementalUnit(@NotNull ElementType elementType);
    
    @Nullable ElementType lastAppliedElement();
    
    @Nullable ElementalAnomalyType lastTriggeredAnomaly();
    
    boolean isElementalAnomalyActive(@NotNull ElementalAnomaly elementalAnomaly);
 
    boolean endElementalAnomaly(@NotNull ElementalAnomaly elementalAnomaly);
    
    int getElementalAnomalyQueueLength(@NotNull ElementalAnomaly elementalAnomaly);
    
}
