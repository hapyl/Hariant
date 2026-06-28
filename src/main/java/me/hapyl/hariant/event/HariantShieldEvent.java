package me.hapyl.hariant.event;

import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.shield.Shield;
import me.hapyl.hariant.entity.shield.ShieldStrength;
import org.jetbrains.annotations.NotNull;

public abstract class HariantShieldEvent extends HariantEvent {
    
    private final Shield shield;
    
    public HariantShieldEvent(@NotNull Shield shield) {
        this.shield = shield;
    }
    
    public @NotNull Shield getShield() {
        return shield;
    }
    
    public @NotNull HariantEntity getEntity() {
        return shield.getEntity();
    }
    
    public @NotNull HariantEntity getApplier() {
        return shield.getApplier();
    }
    
    public @NotNull ShieldStrength getStrength() {
        return shield.getStrength();
    }
    
    public double getMaximumCapacity() {
        return shield.getMaximumCapacity();
    }
    
    public int getDuration() {
        return shield.getDuration();
    }
    
}