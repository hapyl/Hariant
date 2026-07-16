package me.hapyl.hariant.task;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public abstract class HariantTickingStepTask extends HariantTickingTask {
    
    private final int steps;
    
    public HariantTickingStepTask(@NotNull Scheduler scheduler, @Range(from = 0, to = Integer.MAX_VALUE) int steps) {
        super(scheduler);
        
        this.steps = steps;
    }
    
    public abstract boolean run(int tick, int step);
    
    @Override
    public final void run(int tick) {
        for (int step = 0; step < steps; step++) {
            if (this.run(tick, step)) {
                this.cancel();
                return;
            }
        }
    }
    
}
