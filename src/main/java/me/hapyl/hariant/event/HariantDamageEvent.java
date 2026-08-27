package me.hapyl.hariant.event;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.util.Identified;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class HariantDamageEvent extends HariantEvent implements Cancellable, DamageFlagged, MutatesDamage {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final DamageInstance damageInstance;
    private @Nullable Cancel cancel;
    
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
    public void mutateDamage(@NotNull Identified identity, @NotNull DamageMutator mutator, final @NotNull Decimal value) {
        damageInstance.mutateDamage(identity, mutator, value);
    }
    
    @Override
    public boolean isCancelled() {
        return cancel != null;
    }
    
    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel ? Cancel.INSTANCE : null;
    }
    
    public @Nullable Cancel cancel() {
        return cancel;
    }
    
    public void cancel(@NotNull Cancel cancel) {
        this.cancel = cancel;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    public @NotNull static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
    public static @NotNull Cancel cancel(@NotNull Component name) {
        return new Cancel(name);
    }
    
    public static @NotNull Cancel cancel(@NotNull Named named) {
        return cancel(named.getName());
    }
    
    public static class Cancel implements Named {
        
        private static final Cancel INSTANCE = new Cancel(Component.empty());
        
        private final Component name;
        
        Cancel(@NotNull Component name) {
            this.name = name;
        }
        
        @Override
        public @NotNull Component getName() {
            return name;
        }
        
    }
    
}
