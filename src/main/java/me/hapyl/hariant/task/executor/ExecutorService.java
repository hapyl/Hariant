package me.hapyl.hariant.task.executor;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.annotate.SelfReturn;
import me.hapyl.hariant.entity.player.HariantPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

public class ExecutorService implements Executable {
    
    private final Promise promise;
    private final LinkedList<Executable> executables;
    
    @Nullable
    private Executable executable;
    
    public ExecutorService(@NotNull HariantPlayer player) {
        this.promise = new Promise();
        this.executables = Lists.newLinkedList();
        
        // Delegate this service to player
        player.delegate(this);
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
        if (executable != null) {
            executable.cancel();
        }
        
        executables.clear();
    }
    
}
