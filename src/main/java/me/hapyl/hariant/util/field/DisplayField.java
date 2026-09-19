package me.hapyl.hariant.util.field;

import me.hapyl.hariant.util.ComponentFormatter;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * Indicates that the annotated {@link Field} is a display field.
 *
 * <p>
 * Note that the annotated field <b>must</b> implement {@link ComponentFormatter}.
 * </p>
 *
 * <p>
 * Fields annotated by {@link DisplayField} always assumed to be {@link NotNull}.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface DisplayField {
    
    /**
     * Defines an optional name for this {@link DisplayField}; if left empty, the field name will be formatted and used.
     *
     * @return the optional field name, or an empty string to use the field name.
     */
    @NotNull String name() default "";
    
}
