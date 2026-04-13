package me.hapyl.hariant.annotate;

import me.hapyl.hariant.HariantPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.BiFunction;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface StrictNamingConvention {
    
    @NotNull
    String startsWith() default "";
    
    @NotNull
    String endsWith() default "";
    
    interface StrictNamingConventionFunction {
        boolean apply(@NotNull String className, @NotNull String string);
        
        @NotNull
        String reason();
        
        @NotNull
        static StrictNamingConventionFunction create(@NotNull BiFunction<String, String, Boolean> fn, @NotNull String reason) {
            return new StrictNamingConventionFunction() {
                @Override
                public boolean apply(@NotNull String className, @NotNull String string) {
                    return fn.apply(className, string);
                }
                
                @NotNull
                @Override
                public String reason() {
                    return reason;
                }
            };
        }
    }
    
    final class StrictNamingConventionViolationException extends RuntimeException {
        StrictNamingConventionViolationException(@NotNull String message) {
            super(message);
        }
    }
    
    final class Validator {
        public static void validate(@NotNull Object object) {
            final StrictNamingConvention annotation = getSuperClass(object).getAnnotation(StrictNamingConvention.class);
            
            if (annotation == null) {
                throw HariantPlugin.severeExceptionShutdownServer(exception("The provided class does not have %s annotation!".formatted(StrictNamingConvention.class.getSimpleName())));
            }
            
            final String className = object.getClass().getSimpleName();
            
            final String startsWith = annotation.startsWith();
            final String endsWith = annotation.endsWith();
            
            validateOrThrowException(className, startsWith, StrictNamingConventionFunction.create(String::startsWith, "Class name `%s` must start with `%s`!".formatted(className, startsWith)));
            validateOrThrowException(className, endsWith, StrictNamingConventionFunction.create(String::endsWith, "Class name `%s` must end with `%s`!".formatted(className, endsWith)));
        }
        
        @NotNull
        private static Class<?> getSuperClass(@NotNull Object object) {
            Class<?> next = object.getClass();
            
            while (next.getSuperclass() != Object.class) {
                next = next.getSuperclass();
            }
            
            return next;
        }
        
        @NotNull
        private static StrictNamingConventionViolationException exception(@NotNull String reason) {
            return new StrictNamingConventionViolationException(reason);
        }
        
        private static void validateOrThrowException(@NotNull String className, @NotNull String string, @NotNull StrictNamingConventionFunction function) {
            if (!string.isEmpty() && !function.apply(className, string)) {
                throw HariantPlugin.severeExceptionShutdownServer(exception(function.reason()));
            }
        }
    }
    
}
