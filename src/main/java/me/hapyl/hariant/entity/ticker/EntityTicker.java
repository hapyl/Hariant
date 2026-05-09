package me.hapyl.hariant.entity.ticker;

import me.hapyl.eterna.module.util.Predicates;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.util.Resettable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityTicker implements Ticking, Resettable {
    
    /**
     * Defines for how long in the entity is living for.
     */
    public final Ticker life;
    
    /**
     * Defines for how long the entity cannot take any damage.
     *
     * <p>
     * Note that some {@link DamageType} may ignore the invulnerability ticks.
     * </p>
     */
    public final Ticker invulnerability;
    
    private final HariantEntity entity;
    private final List<Ticker> tickerList;
    
    public EntityTicker(@NotNull HariantEntity entity) {
        this.entity = entity;
        
        this.tickerList = List.of(
                life = new TickerImpl("life", TickerType.INCREMENT, Predicates.truthy()),
                invulnerability = new TickerImpl("invulnerability", TickerType.DECREMENT, Predicates.truthy())
        );
    }
    
    @Override
    public void tick() {
        tickerList.forEach(ticker -> ticker.tick(entity));
    }
    
    @Override
    public void reset() {
        tickerList.forEach(Ticker::zero);
    }
}
