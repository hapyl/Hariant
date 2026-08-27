package me.hapyl.hariant.element;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElementHandler {
    
    boolean applyElement(@NotNull ElementSource elementSource);
    
    boolean triggerAnomaly(@NotNull ElementalAnomalySource anomalySource);
    
    double getElementalUnit(@NotNull ElementType elementType);
    
    @Nullable ElementType lastAppliedElement();
    
}
