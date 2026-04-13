package me.hapyl.hariant.task.executor;

import org.jetbrains.annotations.NotNull;

public final class Promise {
    
    private Runnable callback;
    
    public Promise() {
        this.callback = null;
    }
    
    @NotNull
    public Promise then(@NotNull Runnable runnable) {
        this.callback = runnable;
        return this;
    }
    
    public void fulfil() {
        if (this.callback != null) {
            this.callback.run();
            this.callback = null;
        }
    }
    
}
