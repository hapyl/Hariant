package me.hapyl.hariant.event;

import me.hapyl.hariant.entity.damage.DamageInstance;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a damage event, which is called <b>after</b> the damage has been successfully dealt, regardless if entity has died or not.
 *
 * <p>
 * This event exists purely for listening to the final outcome of the damage, and should not mutate the {@link DamageInstance}, as it does
 * nothing for the damage and might break other listeners.
 * </p>
 */
public class HariantDamageEvent extends AbstractHariantDamageEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    public HariantDamageEvent(@NotNull DamageInstance damageInstance) {
        super(damageInstance);
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
}