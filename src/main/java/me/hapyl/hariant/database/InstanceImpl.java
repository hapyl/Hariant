package me.hapyl.hariant.database;

import org.jetbrains.annotations.NotNull;

public class InstanceImpl<O> implements Instance<O> {
    
    protected final PlayerDatabase playerDatabase;
    protected final O origin;
    
    public InstanceImpl(@NotNull PlayerDatabase playerDatabase, @NotNull O origin) {
        this.playerDatabase = playerDatabase;
        this.origin = origin;
    }
    
    @NotNull
    @Override
    public PlayerDatabase getDatabase() {
        return playerDatabase;
    }
    
    @NotNull
    @Override
    public O getOrigin() {
        return origin;
    }
    
    @Override
    public void onInstanceCreated() {
    }
    
    @Override
    public void onInstanceDestroyed() {
    }
    
}
