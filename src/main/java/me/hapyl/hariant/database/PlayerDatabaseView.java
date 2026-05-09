package me.hapyl.hariant.database;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class PlayerDatabaseView extends PlayerDatabase {
    
    public PlayerDatabaseView(@NotNull Database database, @NotNull UUID uuid) {
        super(database, uuid);
    }
    
    @Override
    public void save() {
        throw new UnsupportedOperationException("Mutating database view is not supported!");
    }
    
}
