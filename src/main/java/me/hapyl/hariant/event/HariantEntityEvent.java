package me.hapyl.hariant.event;

import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;

public abstract class HariantEntityEvent extends HariantEvent {
    
    private final HariantEntity entity;
    
    public HariantEntityEvent(@NotNull HariantEntity entity) {
        this.entity = entity;
    }
    
    @NotNull
    public HariantEntity getEntity() {
        return entity;
    }
    
}
