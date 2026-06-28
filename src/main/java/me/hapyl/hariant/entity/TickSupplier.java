package me.hapyl.hariant.entity;

import org.bukkit.Bukkit;

public interface TickSupplier {
    
    int localTicks();
    
    default int globalTicks() {
        return Bukkit.getCurrentTick();
    }
    
}
