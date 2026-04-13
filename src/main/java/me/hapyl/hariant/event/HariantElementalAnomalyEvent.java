package me.hapyl.hariant.event;

import me.hapyl.hariant.element.anomaly.ElementalAnomaly;
import me.hapyl.hariant.entity.HariantEntity;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HariantElementalAnomalyEvent extends HariantEntityEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final ElementalAnomaly elementalAnomaly;
    private final HariantEntity source;
    
    public HariantElementalAnomalyEvent(@NotNull HariantEntity entity, @NotNull ElementalAnomaly elementalAnomaly, @Nullable HariantEntity source) {
        super(entity);
        
        this.elementalAnomaly = elementalAnomaly;
        this.source = source;
    }
    
    @NotNull
    public ElementalAnomaly getElementalAnomaly() {
        return elementalAnomaly;
    }
    
    @Nullable
    public HariantEntity getSource() {
        return source;
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
