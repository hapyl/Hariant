package me.hapyl.hariant.element;

import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public class ElementSourceImpl implements ElementSource {
    
    private final ElementType elementType;
    private final HariantEntity source;
    private final double units;
    
    ElementSourceImpl(@NotNull ElementType elementType, @Nullable HariantEntity source, double units) {
        this.elementType = elementType;
        this.source = source;
        this.units = units;
    }
    
    @NotNull
    @Override
    public ElementType getElementType() {
        return elementType;
    }
    
    @Nullable
    @Override
    public HariantEntity getSource() {
        return source;
    }
    
    @Range(from = 0, to = Integer.MAX_VALUE)
    @Override
    public double getElementUnits() {
        return units;
    }
}
