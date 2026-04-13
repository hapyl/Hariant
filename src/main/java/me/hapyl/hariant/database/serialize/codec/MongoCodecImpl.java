package me.hapyl.hariant.database.serialize.codec;

import org.jetbrains.annotations.NotNull;

public abstract class MongoCodecImpl<T, D> implements MongoCodec<T, D> {
    
    private final Class<D> databaseObjectClass;
    
    MongoCodecImpl(@NotNull Class<D> databaseObjectClass) {
        this.databaseObjectClass = databaseObjectClass;
    }
    
    @NotNull
    @Override
    public Class<D> getDatabaseObjectClass() {
        return databaseObjectClass;
    }
    
}
