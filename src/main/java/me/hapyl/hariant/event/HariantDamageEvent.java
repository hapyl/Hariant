package me.hapyl.hariant.event;

import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.util.Identified;
import me.hapyl.hariant.util.decimal.Decimal;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class HariantDamageEvent extends HariantEvent implements Cancellable, DamageFlagged, MutatesDamage {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final DamageInstance damageInstance;
    private Cancel cancel;
    
    public HariantDamageEvent(@NotNull DamageInstance damageInstance) {
        this.damageInstance = damageInstance;
    }
    
    public @NotNull DamageSource getDamageSource() {
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
        return damageInstance.getDamageSource().getElementType();
    }
    
    public @NotNull DamageType getDamageType() {
        return damageInstance.getDamageSource().getDamageType();
    }
    
    @Override
    public @NotNull Set<? extends DamageFlag> getDamageFlags() {
        return damageInstance.getDamageSource().getDamageFlags();
    }
    
    @Override
    public boolean isFlagged(@NotNull DamageFlag damageFlag) {
        return damageInstance.getDamageSource().isFlagged(damageFlag);
    }
    
    public double getDamage() {
        return damageInstance.getDamage();
    }
    
    @Override
    public void mutateDamage(@NotNull Identified identity, @NotNull DamageMutator mutator, final double value) {
        damageInstance.mutateDamage(identity, mutator, value);
    }
    
    @Override
    public void mutateDamage(@NotNull Identified identity, @NotNull DamageMutator mutator, final Decimal value) {
        damageInstance.mutateDamage(identity, mutator, value);
    }
    
    @Override
    public boolean isCancelled() {
        return cancel != null && cancel.cancel;
    }
    
    /**
     * @deprecated {@link #setCancel(Cancel)}
     */
    @Override
    @Deprecated
    public void setCancelled(boolean cancel) {
        this.cancel = cancel ? Cancel.TRUE : Cancel.FALSE;
    }
    
    public @Nullable Cancel getCancel() {
        return cancel;
    }
    
    public void setCancel(@NotNull Cancel cancel) {
        this.cancel = cancel;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
    public static @NotNull Cancel cancel(boolean startsCooldown, boolean broadcastsImmunity) {
        return new Cancel(true, startsCooldown, broadcastsImmunity);
    }
    
    public static final class Cancel {
        
        public static final Cancel FALSE = new Cancel(false, false, false);
        public static final Cancel TRUE = new Cancel(true, true, true);
        
        private final boolean cancel;
        private final boolean startsCooldown;
        private final boolean broadcastsImmunity;
        
        Cancel(boolean cancel, boolean startsCooldown, boolean broadcastsImmunity) {
            this.cancel = cancel;
            this.startsCooldown = startsCooldown;
            this.broadcastsImmunity = broadcastsImmunity;
        }
        
        public boolean cancel() {
            return cancel;
        }
        
        public boolean startsCooldown() {
            return startsCooldown;
        }
        
        public boolean broadcastsImmunity() {
            return broadcastsImmunity;
        }
        
    }
    
}
