package me.hapyl.hariant.annotate;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated element has a stale value, and instead the value returned by {@link #use()} must be used for a fresh value.
 */
@Target({ ElementType.TYPE_USE, ElementType.FIELD, ElementType.TYPE })
public @interface Stale {
    
    @NotNull String use();
    
}
