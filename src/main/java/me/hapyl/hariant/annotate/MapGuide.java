package me.hapyl.hariant.annotate;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
public @interface MapGuide {
    
    @NotNull
    String key();
    
    @NotNull
    String value();
    
}
