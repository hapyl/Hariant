package me.hapyl.hariant.entity;

import me.hapyl.hariant.HariantConstants;
import org.jetbrains.annotations.NotNull;

public record EffectResistance(boolean value, int tick) {
    
    public EffectResistance(boolean value, @NotNull TickSupplier tickSupplier) {
        this(value, tickSupplier.localTicks());
    }
    
    public boolean hasNotExpired(@NotNull TickSupplier tickSupplier) {
        return tickSupplier.localTicks() - tick < HariantConstants.EFFECT_RESISTANCE_DURATION;
    }
    
}
