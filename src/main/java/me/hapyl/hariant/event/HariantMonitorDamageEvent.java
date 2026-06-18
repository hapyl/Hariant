package me.hapyl.hariant.event;

import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageInstance;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HariantMonitorDamageEvent extends HariantEntityEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final DamageInstance damageInstance;
    
    public HariantMonitorDamageEvent(@NotNull HariantEntity entity, @NotNull DamageInstance damageInstance) {
        super(entity);
        
        this.damageInstance = damageInstance;
    }
    
    @NotNull
    public DamageInstance getDamageInstance() {
        return damageInstance;
    }
    
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
}