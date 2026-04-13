package me.hapyl.hariant.task.executor;

import me.hapyl.hariant.task.HariantTask;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import org.jetbrains.annotations.NotNull;

public class ExecutableWhilstImpl extends ExecutableImpl {
    
    private final While handler;
    
    private HariantTask task;
    
    ExecutableWhilstImpl(@NotNull While handler) {
        super();
        
        this.handler = handler;
    }
    
    @NotNull
    @Override
    public Promise execute() {
        final Promise promise = super.execute();
        
        task = new HariantTickingTask(Scheduler.ofTimer()) {
            @Override
            public void run(int tick) {
                final boolean isBreak = handler.condition(tick) || handler.run(tick);
                
                if (isBreak) {
                    promise.fulfil();
                    this.cancel();
                }
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
