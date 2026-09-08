package me.hapyl.hariant.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class NullableReference<E> {
    
    private @Nullable E referent;
    
    private NullableReference(@Nullable E referent) {
        this.referent = referent;
    }
    
    public void set(@Nullable E referent) throws IllegalStateException {
        if (this.referent != null) {
            throw new IllegalStateException("Reference already set!");
        }
        
        this.referent = referent;
    }
    
    public @Nullable E get() {
        return referent;
    }
    
    public @NotNull Optional<E> getOptional() {
        return Optional.ofNullable(referent);
    }
    
    public @NotNull E getOrThrow() throws NullPointerException {
        return Objects.requireNonNull(referent, "Reference is not set!");
    }
    
    public boolean isSet() {
        return referent != null;
    }
    
    public boolean isEmpty() {
        return referent == null;
    }
    
    public static <E> @NotNull NullableReference<E> empty() {
        return new NullableReference<>(null);
    }
    
    public static <E> @NotNull NullableReference<E> create(@NotNull E referent) {
        return new NullableReference<>(referent);
    }
    
}