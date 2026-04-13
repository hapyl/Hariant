package me.hapyl.hariant.database.serialize.codec;

import me.hapyl.eterna.module.annotate.UtilityClass;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.hariant.util.Timestamp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Function;

@UtilityClass
public final class MongoCodecs {
    
    public static final MongoCodec<UUID, String> UUID;
    public static final MongoCodec<Key, String> KEY;
    public static final MongoCodec<Timestamp, Long> TIMESTAMP;
    
    static {
        UUID = createCodec(String.class, Object::toString, BukkitUtils::getUuidFromString);
        KEY = createCodec(String.class, Key::getKey, Key::ofStringOrNull);
        TIMESTAMP = createCodec(Long.class, Timestamp::getTimestamp, Timestamp::ofEpoch);
    }
    
    private MongoCodecs() {
    }
    
    @NotNull
    private static <T, D> MongoCodec<T, D> createCodec(
            @NotNull Class<D> codecClass,
            @NotNull Function<@NotNull T, @NotNull D> serializeFn,
            @NotNull Function<@NotNull D, @Nullable T> deserializeFn
    ) {
        return new MongoCodec<>() {
            @NotNull
            @Override
            public Class<D> getDatabaseObjectClass() {
                return codecClass;
            }
            
            @NotNull
            @Override
            public D serialize(@NotNull T t) {
                return serializeFn.apply(t);
            }
            
            @Nullable
            @Override
            public T deserialize(@Nullable D d) {
                return d != null ? deserializeFn.apply(d) : null;
            }
        };
    }
    
    
}
