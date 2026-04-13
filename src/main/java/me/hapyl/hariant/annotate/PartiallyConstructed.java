package me.hapyl.hariant.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated parameter is only partially constructed, meaning a reference of itself was
 * passed during it's construction, and may not fully reflect or miss declared fields.
 *
 * <p>
 * Objects that are {@link PartiallyConstructed} should be treated with care.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface PartiallyConstructed {
}

