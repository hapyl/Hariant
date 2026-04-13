package me.hapyl.hariant.entity.effect.status;

import me.hapyl.eterna.module.util.Removable;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.util.TickDuration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StatusEffectInstance implements Ticking, TickDuration, Removable {
    
    private final EnumStatusEffect effect;
    private final HariantEntity entity;
    
    private HariantEntity applier;
    
    private int duration;
    private int tick;
    
    StatusEffectInstance(@NotNull EnumStatusEffect effect, @NotNull HariantEntity entity, @Nullable HariantEntity applier, int duration) {
        this.effect = effect;
        this.entity = entity;
        this.applier = applier;
        this.duration = duration;
        this.tick = duration;
    }
    
    public void mutate(int newDuration, @Nullable HariantEntity newApplier) {
        // Mutate tick only if new duration is longer than CURRENT tick, and current duration isn't infinite
        if (this.duration != HariantConstants.INDEFINITE_DURATION && newDuration > tick) {
            this.duration = newDuration;
            this.tick = newDuration;
        }
        
        // Mutate applier only if it's not null
        if (newApplier != null) {
            this.applier = newApplier;
        }
    }
    
    @NotNull
    public EnumStatusEffect getEffect() {
        return effect;
    }
    
    @NotNull
    public HariantEntity getEntity() {
        return entity;
    }
    
    @Nullable
    public HariantEntity getApplier() {
        return applier;
    }
    
    @Override
    public void tick() {
        if (this.duration != HariantConstants.INDEFINITE_DURATION) {
            this.tick--;
        }
        
        this.effect.onTick(entity, applier, tick);
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
        return duration != HariantConstants.INDEFINITE_DURATION && tick <= 0;
    }
    
    @Override
    public void remove() {
        this.effect.onRemove(entity, applier);
    }
    
}
