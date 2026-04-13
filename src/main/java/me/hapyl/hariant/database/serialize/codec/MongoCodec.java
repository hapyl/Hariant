package me.hapyl.hariant.database.serialize.codec;

import me.hapyl.hariant.database.PlayerDatabase;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Represents a codec for a custom type to be stored in the {@link PlayerDatabase}.
 *
 * @param <T> - The actual object type.
 * @param <D> - The type of the object stored in the database.
 */
public interface MongoCodec<T, D> {
    
    @NotNull
    Class<D> getDatabaseObjectClass();
    
    @NotNull
    D serialize(@NotNull T t);
    
    @Nullable
    T deserialize(@Nullable D d);
    
    @NotNull
    default Optional<T> read(@NotNull Document document, @NotNull String key) {
        final D d = document.get(key, getDatabaseObjectClass());
        
        return d != null ? Optional.ofNullable(deserialize(d)) : Optional.empty();
    }
    
    default void write(@NotNull Document document, @NotNull String key, @NotNull T t) {
        document.put(key, serialize(t));
    }
    
}
