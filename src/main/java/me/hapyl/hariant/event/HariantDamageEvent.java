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

public class HariantDamageEvent extends HariantEvent implements Cancellable, DamageFlagged {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final DamageInstance damageInstance;
    
    private boolean cancel;
    private boolean startCooldownIfCancelled;
    
    public HariantDamageEvent(@NotNull DamageInstance damageInstance) {
        this.damageInstance = damageInstance;
    }
    
    /**
     * Gets the {@link DamageSource} of the event.
     *
     * <p>
     * Note that {@link HariantDamageCalculationsEvent} may modify the damage source instance, therefore you should not do a {@code instanceof} check,
     * since it may fail if the damage source was modified, instead you should use {@link DamageSource#compareIdentity(DamageSourceIdentity)}.
     * </p>
     *
     * @return the current damage source of the event.
     */
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
    
    public void mutateDamage(@NotNull Identified identity, @NotNull DamageMutator mutator, final double value) {
        damageInstance.mutateDamage(identity, mutator, value);
    }
    
    public void mutateDamage(@NotNull Identified identity, @NotNull DamageMutator mutator, final Decimal value) {
        damageInstance.mutateDamage(identity, mutator, value);
    }
    
    @Override
    public boolean isCancelled() {
        return cancel;
    }
    
    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
    
    public void setCancelled(boolean cancel, boolean startCooldownIfCancelled) {
        this.cancel = cancel;
        this.startCooldownIfCancelled = startCooldownIfCancelled;
    }
    
    public boolean isStartCooldownIfCancelled() {
        return startCooldownIfCancelled;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
}
