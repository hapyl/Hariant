package me.hapyl.hariant.task.executor;

import me.hapyl.hariant.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;
import java.util.function.Predicate;

/**
 * Represents an abstract implementation of a native {@code while} loop that runs over the tick duration on the main thread.
 */
public interface While {
    
    /**
     * Gets whether this {@link While} should be interrupted.
     *
     * @param tick - The current tick.
     * @return {@code true} if this while loop should be interrupted; {@code false} otherwise.
     */
    boolean condition(int tick);
    
    /**
     * Runs this {@link While} loop.
     *
     * @param tick - The current tick.
     * @return {@code true} if this while loop should be interrupted; {@code false} otherwise.
     */
    boolean run(int tick);
    
    /**
     * Creates a {@link While} loop.
     *
     * @param condition - The condition of the loop.
     * @param function  - The function of the loop; returning {@code true} acts the same as {@code break} keyword.
     * @return a new while loop.
     */
    @NotNull
    static While whilst(@NotNull Predicate<Integer> condition, @NotNull IntFunction<@NotNull Boolean> function) {
        return new WhileImpl(condition, function);
    }
    
    /**
     * Creates a {@link While} that runs for the given {@link Duration}.
     *
     * @param duration - The duration.
     * @param function - The function of the loop; returning {@code true} acts the same as {@code break} keyword.
     * @return a new while loop.
     */
    @NotNull
    static While duration(@NotNull Duration duration, @NotNull IntFunction<@NotNull Boolean> function) {
        return new WhileImpl(tick -> tick >= duration.getDuration(), function);
    }
    
    /**
     * Creates a {@link While} that always runs.
     *
     * @param function - The function of the loop; returning {@code true} acts the same as {@code break} keyword.
     * @return a new while loop.
     */
    @NotNull
    static While whileTrue(@NotNull IntFunction<@NotNull Boolean> function) {
        return new WhileImpl(_ -> true, function);
    }
    
}
