package me.hapyl.hariant.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated constructor is used for creating a copy of a given class.
 */
@Target({ ElementType.CONSTRUCTOR })
public @interface CopyConstructor {
}
