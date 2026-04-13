package me.hapyl.hariant.task.executor;

import me.hapyl.hariant.task.Cancellable;
import me.hapyl.hariant.task.Scheduler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface Executable extends Cancellable {
    
    @NotNull
    Promise execute();
    
    @Override
    void cancel();
    
    @NotNull
    static Executable execute(@NotNull Runnable runnable) {
        return new ExecutableScheduledImpl(runnable, Scheduler.ofNow());
    }
    
    @NotNull
    static Executable later(@NotNull Runnable runnable, final int delay) {
        return new ExecutableScheduledImpl(runnable, Scheduler.ofDelayed(delay));
    }
    
    @NotNull
    static Executable whilst(@NotNull While handler) {
        return new ExecutableWhilstImpl(handler);
    }
    
    @NotNull
    static Executable await(@NotNull Consumer<Promise> consumer) {
        return new ExecutableAwaitImpl(consumer);
    }
    
}
