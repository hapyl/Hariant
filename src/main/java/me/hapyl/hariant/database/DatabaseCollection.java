package me.hapyl.hariant.database;

import org.jetbrains.annotations.NotNull;

public enum DatabaseCollection {
    
    PLAYERS("players", false),
    SECURITY("security", true),
    
    ;
    
    private final String collectionName;
    private final boolean async;
    
    DatabaseCollection(@NotNull String collectionName, boolean async) {
        this.collectionName = collectionName;
        this.async = async;
    }
    
    @NotNull
    public String getCollectionName() {
        return collectionName;
    }
    
    public boolean isAsync() {
        return async;
    }
}
