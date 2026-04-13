package me.hapyl.hariant.database;

import org.jetbrains.annotations.NotNull;

public interface Instance<O> {
    
    @NotNull
    PlayerDatabase getDatabase();
    
    @NotNull
    O getOrigin();
    
}
