package me.hapyl.hariant.element;

import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public interface ElementSource {
    
    @NotNull
    ElementType getElementType();
    
    @Nullable
    HariantEntity getSource();
    
    @Range(from = 0, to = Integer.MAX_VALUE)
    double getElementUnits();
    
    @NotNull
    static ElementSource create(@NotNull ElementType elementType, @Nullable HariantEntity source, double units) {
        return new ElementSourceImpl(elementType, source, units);
    }
}
