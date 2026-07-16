package me.hapyl.hariant.util;

import org.jetbrains.annotations.NotNull;

public record Either<K>(@NotNull K left, @NotNull K right) {
    
    public static <K> @NotNull Either<K> either(@NotNull K eitherThis, @NotNull K eitherThat) {
        return new Either<>(eitherThis, eitherThat);
    }
    
}
