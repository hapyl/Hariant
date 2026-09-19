package me.hapyl.hariant.element;

import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.util.decimal.Decimal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public interface ElementSource extends ElementalAnomalySource {
    
    @NotNull ElementType getElementType();
    
    @Nullable HariantEntity getSource();
    
    @Range(from = 0, to = Integer.MAX_VALUE)
    double getElementUnits();
    
    @Override
    default @NotNull ElementalAnomalyType getElementalAnomaly() {
        return this.getElementType().getElementalAnomaly();
    }
    
    static @NotNull ElementSource create(@NotNull ElementType elementType, @Nullable HariantEntity source, double units) {
        return new ElementSourceImpl(elementType, source, units);
    }
    
    static @NotNull ElementSource create(@NotNull ElementType elementType, @Nullable HariantEntity source, @NotNull Decimal units) {
        return create(elementType, source, units.doubleValue());
    }
    
}