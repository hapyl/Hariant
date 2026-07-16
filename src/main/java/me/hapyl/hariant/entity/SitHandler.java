package me.hapyl.hariant.entity;

import me.hapyl.eterna.module.annotate.EventLike;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface SitHandler {
    
    boolean allowDismount();
    
    void move(@NotNull Location location);
    
    @EventLike
    void onMount();
    
    @EventLike
    void onDismount();
    
}
