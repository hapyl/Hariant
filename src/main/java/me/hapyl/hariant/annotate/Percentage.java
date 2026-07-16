package me.hapyl.hariant.annotate;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER })
public @interface Percentage {
    
    @NotNull
    Type value();
    
    enum Type {
        /**
         * Defines that the percentage type must be passed as a whole number, eg: {@code (30 -> 30%)}
         */
        WHOLE_NUMBER,
        
        /**
         * Defines that the percentage type must be passed as a decimal, eg: {@code (0.3 -> 30%)}
         */
        DECIMAL
    }
    
}
