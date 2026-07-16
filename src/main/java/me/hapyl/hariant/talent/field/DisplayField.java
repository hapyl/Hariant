package me.hapyl.hariant.talent.field;

import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.util.ComponentFormatter;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * Defines an annotation that may be applied to {@link Field}s on applicable classes (eg: {@link Talent}) to mark the field
 * to be added to the description of the that class in the game.
 *
 * <p>
 * Note that the field type <b>must</b> implement {@link ComponentFormatter}.
 * </p>
 *
 * <p>
 * Fields annotated by {@link DisplayField} always assumed to be @{@link NotNull}.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface DisplayField {
    
    /**
     * Defines an optional name for this {@link DisplayField}; if left empty, the field code name will be formatted and used.
     *
     * @return the optional field name, or an empty string to use the field code name.
     */
    @NotNull
    String name() default "";
    
}
