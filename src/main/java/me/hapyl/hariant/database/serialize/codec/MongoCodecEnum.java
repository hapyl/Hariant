package me.hapyl.hariant.database.serialize.codec;

import me.hapyl.eterna.module.util.Enums;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MongoCodecEnum<E extends Enum<E>> implements MongoCodec<E, String> {
    
    private final Class<E> enumClass;
    
    MongoCodecEnum(@NotNull Class<E> enumClass) {
        this.enumClass = enumClass;
    }
    
    @Override
    public @NotNull Class<String> getDatabaseObjectClass() {
        return String.class;
    }
    
    @Override
    public @NotNull String serialize(@NotNull E e) {
        return e.name().toLowerCase();
    }
    
    @Override
    public @Nullable E deserialize(@Nullable String string) {
        return string != null ? Enums.byName(enumClass, string) : null;
    }
    
}