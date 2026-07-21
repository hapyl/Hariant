package me.hapyl.hariant.event.effect;

import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.effect.status.StatusEffectType;
import me.hapyl.hariant.event.HariantEntityEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HariantStatusEffectRemoveEvent extends HariantEntityEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final StatusEffectType statusEffectType;
    
    public HariantStatusEffectRemoveEvent(@NotNull HariantEntity entity, @NotNull StatusEffectType statusEffectType) {
        super(entity);
        
        this.statusEffectType = statusEffectType;
    }
    
    public @NotNull StatusEffectType getStatusEffectType() {
        return statusEffectType;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
}
