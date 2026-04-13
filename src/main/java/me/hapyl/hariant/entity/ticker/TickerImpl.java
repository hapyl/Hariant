package me.hapyl.hariant.entity.ticker;

import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class TickerImpl implements Ticker {
    
    private final String identity;
    private final TickerType tickerType;
    private final Predicate<HariantEntity> predicate;
    
    private int value;
    
    TickerImpl(@NotNull String identity, @NotNull TickerType tickerType, @NotNull Predicate<HariantEntity> predicate) {
        this.identity = identity;
        this.tickerType = tickerType;
        this.predicate = predicate;
    }
    
    @NotNull
    @Override
    public String identify() {
        return identity;
    }
    
    @NotNull
    @Override
    public TickerType tickerType() {
        return tickerType;
    }
    
    @Override
    public void tick(@NotNull HariantEntity entity) {
        final int previousValue = value;
        
        value = predicate.test(entity) ? Math.max(0, value + tickerType.increment()) : 0;
        
        if (value != previousValue) {
            onTick(previousValue);
        }
    }
    
    @Override
    public int value() {
        return this.value;
    }
    
    @Override
    public int value(int value) {
        return this.value = value;
    }
    
    @Override
    public void onTick(int value) {
    }
    
}
