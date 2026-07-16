package me.hapyl.hariant.task.executor;

import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.Cancellable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface Executable extends Cancellable {
    
    @NotNull
    Promise execute();
    
    @Override
    void cancel();
    
    /**
     * Creates an {@link Executable} that executes the given {@link Runnable} at the next game tick.
     *
     * @param runnable - The runnable to execute.
     * @return a new executable.
     */
    static @NotNull Executable execute(@NotNull Runnable runnable) {
        return new ExecutableScheduledImpl(runnable, Scheduler.ofNow());
    }
    
    /**
     * Creates an {@link Executable} that executes the given {@link Runnable} after the given delay.
     *
     * @param runnable - The runnable to execute.
     * @param delay    - The delay, in ticks.
     * @return a new executable.
     */
    static @NotNull Executable later(@NotNull Runnable runnable, final int delay) {
        return new ExecutableScheduledImpl(runnable, Scheduler.ofDelayed(delay));
    }
    
    /**
     * Creates an {@link Executable} that executes the given {@link While} loop.
     *
     * @param handler - The while loop handler.
     * @return a new executable.
     */
    static @NotNull Executable whilst(@NotNull While handler) {
        return new ExecutableWhilstImpl(handler);
    }
    
    /**
     * Creates an {@link Executable} that executes the given code whenever the given {@link Promise} is fulfilled.
     *
     * @param consumer - The consumer of the promise that must be fulfilled.
     * @return a new executable.
     */
    static @NotNull Executable await(@NotNull Consumer<Promise> consumer) {
        return new ExecutableAwaitImpl(consumer);
    }
    
    /**
     * Creates an {@link Executable} that delegate cancelling of it to the given {@link Cancellable}.
     *
     * @param cancellable - The cancellable to delegate to.
     * @return a new executable.
     */
    static @NotNull Executable delegate(@NotNull Cancellable cancellable) {
        return new ExecutableDelegateImpl(cancellable);
    }
    
}
