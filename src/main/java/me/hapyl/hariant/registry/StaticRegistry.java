package me.hapyl.hariant.registry;

import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.hariant.HariantPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class StaticRegistry<K extends Keyed> {
    
    private static final String EXPECTED_METHOD_NAME = "getRegistry";
    
    protected static <K extends Keyed, R extends StaticRegistry<K>> StaticRegistryMap<K> requestRegistry(@NotNull Class<R> clazz) {
        // Require `final` modifier
        if (!Modifier.isFinal(clazz.getModifiers())) {
            throw HariantPlugin.severeExceptionShutdownServer(new RuntimeException("Static registry `%s` class must be `final`!".formatted(clazz.getSimpleName())));
        }
        
        // Require 'getRegistry()' method
        try {
            final Method method = clazz.getMethod(EXPECTED_METHOD_NAME);
            final Class<?> returnType = method.getReturnType();
            
            if (!StaticRegistryMap.class.isAssignableFrom(returnType)) {
                throw HariantPlugin.severeExceptionShutdownServer(new IllegalArgumentException(
                        "Return type of `%s` must be `%s`, not `%s`!".formatted(EXPECTED_METHOD_NAME, StaticRegistryMap.class.getSimpleName(), returnType.getSimpleName())
                ));
            }
            
            final int modifiers = method.getModifiers();
            
            if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers)) {
                throw HariantPlugin.severeExceptionShutdownServer(new RuntimeException("Method `%s` must be `public` and `static`!".formatted(EXPECTED_METHOD_NAME)));
            }
        }
        catch (NoSuchMethodException noSuchMethodException) {
            throw HariantPlugin.severeExceptionShutdownServer(new RuntimeException("Missing `public static` `%s` method in `%s`!".formatted(EXPECTED_METHOD_NAME, clazz.getSimpleName())));
        }
        
        return new StaticRegistryMap<>();
    }
    
    
}
