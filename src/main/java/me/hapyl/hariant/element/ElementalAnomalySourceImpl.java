package me.hapyl.hariant.element;

import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElementalAnomalySourceImpl implements ElementalAnomalySource {
    
    private final ElementalAnomalyType elementalAnomaly;
    private final HariantEntity source;
    
    public ElementalAnomalySourceImpl(@NotNull ElementalAnomalyType elementalAnomaly, @Nullable HariantEntity source) {
        this.elementalAnomaly = elementalAnomaly;
        this.source = source;
    }
    
    @Override
    public @NotNull ElementalAnomalyType getElementalAnomaly() {
        return elementalAnomaly;
    }
    
    @Override
    public @Nullable HariantEntity getSource() {
        return source;
    }
    
}