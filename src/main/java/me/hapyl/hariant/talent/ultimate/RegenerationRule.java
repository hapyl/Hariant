package me.hapyl.hariant.talent.ultimate;

import me.hapyl.hariant.attribute.AttributeType;
import org.jetbrains.annotations.Nullable;

public interface RegenerationRule {
    
    double regeneratePassively();
    
    double regenerateOnElimination();
    
    @Nullable
    AttributeType getEffectiveAttribute();
    
}
