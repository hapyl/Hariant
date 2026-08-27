package me.hapyl.hariant.element;

import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElementalAnomalySource {
    
    @NotNull ElementalAnomalyType getElementalAnomaly();
    
    @Nullable HariantEntity getSource();
    
    static @NotNull ElementalAnomalySource create(@NotNull ElementalAnomalyType elementalAnomaly, @Nullable HariantEntity source) {
        return new ElementalAnomalySourceImpl(elementalAnomaly, source);
    }
    
}