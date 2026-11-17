package me.hapyl.hariant.talent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TalentContext {
    
    <T> @NotNull T retrieve(@NotNull Class<T> clazz) throws ClassCastException;
    
    static @NotNull TalentContext create(@NotNull Object object) {
        return new TalentContextImpl(object);
    }
    
    static @Nullable TalentContext createNullable(@Nullable Object object) {
        return object != null ? create(object) : null;
    }
    
}
