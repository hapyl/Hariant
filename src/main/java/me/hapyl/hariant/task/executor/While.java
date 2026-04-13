package me.hapyl.hariant.task.executor;

import me.hapyl.eterna.module.util.Predicates;
import me.hapyl.hariant.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;
import java.util.function.Predicate;

/**
 * Represents an abstract implementation of a native {@code while} loop that runs over the tick duration on the main thread.
 */
public interface While {
    
    boolean condition(int tick);
    
    boolean run(int tick);
    
    @NotNull
    static While whilst(@NotNull Predicate<Integer> condition, @NotNull IntFunction<@NotNull Boolean> function) {
        return new WhileImpl(condition, function);
    }
    
    @NotNull
    static While duration(@NotNull Duration duration, @NotNull IntFunction<@NotNull Boolean> function) {
        return new WhileImpl(tick -> tick >= duration.getDuration(), function);
    }
    
    @NotNull
    static While whileTrue(@NotNull IntFunction<@NotNull Boolean> function) {
        return new WhileImpl(Predicates.truthy(), function);
    }
    
}
