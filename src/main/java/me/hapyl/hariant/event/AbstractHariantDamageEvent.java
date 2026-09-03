package me.hapyl.hariant.event;

import me.hapyl.hariant.annotate.Stale;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

/**
 * Represents an abstract damage event, split into three events that are responsible for damage calculations.
 *
 * @see HariantDamageCalculationsEvent
 * @see HariantDamageComputeEvent
 * @see HariantDamageEvent
 */
public abstract class AbstractHariantDamageEvent extends HariantEvent implements DamageFlagged {
    
    protected final DamageInstance damageInstance;
    
    AbstractHariantDamageEvent(@NotNull DamageInstance damageInstance) {
        this.damageInstance = damageInstance;
    }
    
    public @NotNull DamageInstance getDamageInstance() {
        return damageInstance;
    }
    
    public @NotNull @Stale(use = "getDamageInstance") DamageSource getDamageSource() {
        return damageInstance.getDamageSource();
    }
    
    public @NotNull HariantEntity getEntity() {
        return damageInstance.getEntity();
    }
    
    public @Nullable HariantEntity getAttacker() {
        return damageInstance.getAttacker();
    }
    
    public boolean isCritical() {
        return damageInstance.isCritical();
    }
    
    public @NotNull ElementType getElementType() {
        return damageInstance.getElementType();
    }
    
    public @NotNull DamageType getDamageType() {
        return damageInstance.getDamageType();
    }
    
    public double getDamage() {
        return damageInstance.getDamage();
    }
    
    @Override
    public @NotNull @Unmodifiable Set<? extends DamageFlag> getDamageFlags() {
        return damageInstance.getDamageFlags();
    }
    
    @Override
    public boolean isFlagged(@NotNull DamageFlag damageFlag) {
        return damageInstance.isFlagged(damageFlag);
    }
    
}