package me.hapyl.hariant.entity.heal;

import org.jetbrains.annotations.NotNull;

public record HealingOutcome(@NotNull Type type, double healthBeforeHealing, double healthAfterHealing, double actualHealing, double excessHealing) {
    
    public boolean hasHealed() {
        return type == Type.HEALED;
    }
    
    public boolean hasNotHealed() {
        return type == Type.NOT_HEALED;
    }
    
    public boolean hasExcessHealing() {
        return excessHealing > 0;
    }
    
    public enum Type {
        HEALED,
        NOT_HEALED
    }
    
}
