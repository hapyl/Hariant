package me.hapyl.hariant.event;

import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.Liquid;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HariantEntityLiquidEvent extends HariantEntityEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final Liquid previousLiquid;
    private final Liquid liquid;
    
    public HariantEntityLiquidEvent(@NotNull HariantEntity entity, @Nullable Liquid previousLiquid, @Nullable Liquid liquid) {
        super(entity);
        
        this.previousLiquid = previousLiquid;
        this.liquid = liquid;
    }
    
    public @Nullable Liquid getPreviousLiquid() {
        return previousLiquid;
    }
    
    public @Nullable Liquid getLiquid() {
        return liquid;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
}