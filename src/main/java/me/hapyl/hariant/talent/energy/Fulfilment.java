package me.hapyl.hariant.talent.energy;

import me.hapyl.eterna.module.math.Tick;
import org.jetbrains.annotations.NotNull;

public interface Fulfilment {
    
    @NotNull Tick cooldown();
    
    int threshold();
    
    static @NotNull Fulfilment fulfilment(@NotNull Tick tick, int fulfilment) {
        return new FulfilmentImpl(tick, fulfilment);
    }
    
    static @NotNull Fulfilment always() {
        return FulfilmentImpl.ALWAYS;
    }
    
    static @NotNull Fulfilment defaultFulfilment() {
        return FulfilmentImpl.DEFAULT;
    }
    
}