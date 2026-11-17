package me.hapyl.hariant.talent.energy;

import me.hapyl.eterna.module.math.Tick;
import org.jetbrains.annotations.NotNull;

public final class FulfilmentImpl implements Fulfilment {
    
    static final Fulfilment DEFAULT = new FulfilmentImpl(Tick.ofSeconds(2.5f), 2);
    static final Fulfilment ALWAYS = new FulfilmentImpl(Tick.ofTicks(0), 0);
    
    private final Tick cooldown;
    private final int threshold;
    
    FulfilmentImpl(@NotNull Tick cooldown, int threshold) {
        this.cooldown = cooldown;
        this.threshold = threshold;
    }
    
    @Override
    public @NotNull Tick cooldown() {
        return cooldown;
    }
    
    @Override
    public int threshold() {
        return threshold;
    }
    
}