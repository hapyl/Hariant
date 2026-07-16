package me.hapyl.hariant.hero.pytaria.bee;

import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;

public class BeeTarget {
    
    private final HariantEntity target;
    private int chasingFor;
    
    BeeTarget(@NotNull HariantEntity target) {
        this.target = target;
    }
    
    public @NotNull HariantEntity getEntity() {
        return target;
    }
    
    public int incrementChasingFor() {
        return this.chasingFor++;
    }
    
}
