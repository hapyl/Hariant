package me.hapyl.hariant.task.executor;

import me.hapyl.hariant.task.InternalTasks;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ExecutableAwaitImpl extends ExecutableImpl {
    
    private final Consumer<Promise> consumer;
    
    ExecutableAwaitImpl(@NotNull Consumer<Promise> consumer) {
        super();
        this.consumer = consumer;
    }
    
    @NotNull
    @Override
    public Promise execute() {
        final Promise promise = super.execute();
        InternalTasks.now(() -> consumer.accept(promise));
        
        return promise;
    }
    
    @Override
    public void cancel() {
    }
    
}
