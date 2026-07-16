package me.hapyl.hariant.task.executor;

import org.jetbrains.annotations.NotNull;

public final class Promise {
    
    private Runnable callback;
    
    public Promise() {
        this.callback = null;
    }
    
    public void then(@NotNull Runnable runnable) {
        this.callback = runnable;
    }
    
    public void fulfil() {
        if (this.callback != null) {
            this.callback.run();
            this.callback = null;
        }
    }
    
}
