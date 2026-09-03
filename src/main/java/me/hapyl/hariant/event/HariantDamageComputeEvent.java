package me.hapyl.hariant.event;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.hariant.entity.damage.DamageInstance;
import me.hapyl.hariant.entity.damage.MutatesDamage;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.util.Identified;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a damage event, which is called after damage calculations but before the damage is dealt.
 *
 * <p>
 * The event allows mutating the {@code damage} of the {@link DamageInstance}, as well as cancelling, which will prevent
 * the damage from being dealt to the entity.
 * </p>
 *
 * <p>
 * This event is <b>not</b> fired for {@code Ferocity} hits!
 * </p>
 */
public class HariantDamageComputeEvent extends AbstractHariantDamageEvent implements Cancellable, MutatesDamage {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private @Nullable Cancel cancel;
    
    public HariantDamageComputeEvent(@NotNull DamageInstance damageInstance) {
        super(damageInstance);
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
    
    public @NotNull
    static HandlerList getHandlerList() {
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
