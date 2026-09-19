package me.hapyl.hariant.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated fields must be copied before passing as an argument, either via a {@link CopyConstructor} or a static method (if exists).
 */
@Target({ ElementType.FIELD })
public @interface CopyOnPass {
}
