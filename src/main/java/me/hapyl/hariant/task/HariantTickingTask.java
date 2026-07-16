package me.hapyl.hariant.task;

import me.hapyl.hariant.util.decimal.Decimal;
import org.jetbrains.annotations.NotNull;

public abstract class HariantTickingTask extends HariantTask {
    
    private int tick;
    
    public HariantTickingTask(@NotNull Scheduler scheduler) {
        super(scheduler);
    }
    
    public abstract void run(final int tick);
    
    @Override
    public final void run() {
        this.run(this.tick++);
    }
    
    public int currentTick() {
        return tick;
    }
    
    protected boolean modulo(final int modulo) {
        return tick % modulo == 0;
    }
    
    protected boolean modulo(@NotNull Decimal decimal) {
        return this.modulo(decimal.intValue());
    }
    
}
