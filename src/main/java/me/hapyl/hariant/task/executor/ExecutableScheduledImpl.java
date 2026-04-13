package me.hapyl.hariant.task.executor;

import me.hapyl.hariant.task.HariantTask;
import me.hapyl.hariant.task.Scheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExecutableScheduledImpl extends ExecutableImpl {
    
    private final Runnable runnable;
    private final Scheduler scheduler;
    
    @Nullable
    private HariantTask task;
    
    ExecutableScheduledImpl(@NotNull Runnable runnable, @NotNull Scheduler scheduler) {
        super();
        
        this.runnable = runnable;
        this.scheduler = scheduler;
    }
    
    @NotNull
    @Override
    public Promise execute() {
        final Promise promise = super.execute();
        
        task = new HariantTask(scheduler) {
            @Override
            public void run() {
                runnable.run();
                promise.fulfil();
            }
        };
        
        return promise;
    }
    
    @Override
    public void cancel() {
        if (task != null) {
            task.cancel();
        }
    }
}
