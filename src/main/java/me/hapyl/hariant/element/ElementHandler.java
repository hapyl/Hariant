package me.hapyl.hariant.element;

import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElementHandler {
    
    void applyElement(@NotNull ElementSource elementSource);
    
    double getElementalUnit(@NotNull ElementType elementType);
    
    void triggerAnomaly(@NotNull ElementalAnomalyType elementalAnomaly, @Nullable HariantEntity source);
    
}
