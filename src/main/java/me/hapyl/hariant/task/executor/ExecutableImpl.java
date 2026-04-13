package me.hapyl.hariant.task.executor;

import org.jetbrains.annotations.NotNull;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class ExecutableImpl implements Executable {
    
    // Private to enforce calling `super.execute()`
    private final Promise promise;
    
    ExecutableImpl() {
        this.promise = new Promise();
    }
    
    @NotNull
    @Override
    @OverridingMethodsMustInvokeSuper
    public Promise execute() {
        return promise;
    }
    
    // Enforce `cancel()` impl
    @Override
    public abstract void cancel();
    
}
