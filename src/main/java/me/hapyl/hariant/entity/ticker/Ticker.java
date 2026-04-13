package me.hapyl.hariant.entity.ticker;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.TickingEntity;
import me.hapyl.hariant.util.Identified;
import org.jetbrains.annotations.NotNull;

public interface Ticker extends TickingEntity, Identified {
    
    @NotNull
    @Override
    String identify();
    
    @NotNull
    TickerType tickerType();
    
    @Override
    void tick(@NotNull HariantEntity entity);
    
    int value();
    
    int value(final int value);
    
    default void zero() {
        this.value(0);
    }
    
    @EventLike
    void onTick(final int value);
    
}
