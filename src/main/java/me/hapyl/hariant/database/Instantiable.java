package me.hapyl.hariant.database;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface Instantiable {
    
    @NotNull
    Object newInstance(@NotNull PlayerDatabase database, @NotNull UUID uuid);
    
}
