package me.hapyl.hariant.task;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.hariant.util.decimal.Decimal;
import org.jetbrains.annotations.NotNull;

public abstract class HariantTickingTask extends HariantTask {
    
    private int tick;
    
    public HariantTickingTask(@NotNull Scheduler builder) {
        super(builder);
    }
    
    public abstract void run(final int tick);
    
    @Override
    public final void run() {
        if (this.tick == 0) {
            this.onFirstTick();
        }
        
        this.run(this.tick++);
    }
    
    @EventLike
    public void onFirstTick() {
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
