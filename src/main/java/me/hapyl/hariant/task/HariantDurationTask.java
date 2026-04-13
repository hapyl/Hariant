package me.hapyl.hariant.task;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.hariant.util.Duration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public abstract class HariantDurationTask extends HariantTickingTask {
    
    private final int duration;
    
    public HariantDurationTask(@Range(from = 0, to = Integer.MAX_VALUE) int duration) {
        super(Scheduler.ofTimer(1));
        
        this.duration = duration;
    }
    
    public HariantDurationTask(@NotNull Duration duration) {
        this(duration.getDuration());
    }
    
    public abstract void run(int tick, int duration);
    
    @Override
    public final void run(int tick) {
        if (tick >= this.duration) {
            this.onLastTick();
            this.cancel();
            return;
        }
        
        this.run(tick, duration);
    }
    
    @EventLike
    public void onLastTick() {
    }
    
    public boolean isLastTick() {
        return currentTick() == duration - 1;
    }
    
    public int duration() {
        return duration;
    }
    
    public double progress() {
        return (double) currentTick() / duration;
    }
    
}
