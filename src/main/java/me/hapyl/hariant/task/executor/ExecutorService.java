package me.hapyl.hariant.task.executor;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.annotate.SelfReturn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

/**
 * The {@link ExecutorService} is an {@link Executable} that allows chaining {@link Executable} calls.
 */
public class ExecutorService implements Executable {
    
    private final Promise promise;
    private final LinkedList<Executable> executables;
    
    @Nullable
    private Executable executable;
    
    public ExecutorService() {
        this.promise = new Promise();
        this.executables = Lists.newLinkedList();
    }
    
    @SelfReturn
    public ExecutorService then(@NotNull Executable executable) {
        this.executables.add(executable);
        return this;
    }
    
    @NotNull
    @Override
    public Promise execute() {
        executable = executables.pollFirst();
        
        // Either no tasks or finished execution, complete either way
        if (executable == null) {
            promise.fulfil();
        }
        // Otherwise execute tasks recursively
        else {
            executable.execute().then(this::execute);
        }
        
        return promise;
    }
    
    @Override
    public void cancel() {
        // Cancel current executable
        if (executable != null) {
            executable.cancel();
        }
        
        executables.clear();
        promise.fulfil();
    }
    
}
