package me.hapyl.hariant.task.executor;

import me.hapyl.hariant.util.Cancellable;
import org.jetbrains.annotations.NotNull;

public class ExecutableDelegateImpl extends ExecutableImpl {
    
    private final Cancellable cancellable;
    
    public ExecutableDelegateImpl(@NotNull Cancellable cancellable) {
        super();
        this.cancellable = cancellable;
    }
    
    @Override
    public void cancel() {
        this.cancellable.cancel();
    }
    
}