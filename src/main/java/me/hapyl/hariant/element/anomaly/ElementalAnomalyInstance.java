package me.hapyl.hariant.element.anomaly;

import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.element.ElementalAnomalySource;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.ui.ComponentDisplay;
import me.hapyl.hariant.util.TickingDown;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class ElementalAnomalyInstance implements Ticking, TickingDown {
    
    protected final ElementalAnomalySource anomalySource;
    protected final HariantEntity entity;
    protected final HariantEntity source;
    
    private final int duration;
    private int tick;
    
    ElementalAnomalyInstance(@NotNull ElementalAnomalySource anomalySource, @NotNull HariantEntity entity, @Nullable HariantEntity source, int duration) {
        this.anomalySource = anomalySource;
        this.entity = entity;
        this.source = source;
        this.duration = duration;
        this.tick = duration;
    }
    
    public @NotNull ElementalAnomalySource getAnomalySource() {
        return anomalySource;
    }
    
    public @NotNull ElementalAnomalyType getElementalAnomaly() {
        return anomalySource.getElementalAnomaly();
    }
    
    public @NotNull HariantEntity getEntity() {
        return entity;
    }
    
    public @Nullable HariantEntity getSource() {
        return source;
    }
    
    @Override
    public int currentTick() {
        return tick;
    }
    
    @Override
    public int duration() {
        return duration;
    }
    
    @Override
    public boolean isOver() {
        return tick <= 0;
    }
    
    @OverridingMethodsMustInvokeSuper
    @Override
    public void tick() {
        tick--;
    }
    
    public void onStart() {
        // Display
        ComponentDisplay.ofAscend(this.anomalySource.getElementalAnomaly().asComponent(), this.entity.getMidpointLocation(), 40, 1.0f);
        
        // Fx
        this.entity.playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 2.0f);
    }
    
    public void onEnd() {
    }
    
}