package me.hapyl.hariant.entity.player;

import me.hapyl.hariant.entity.DelegateCancellable;
import me.hapyl.hariant.util.Cancellable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public interface Delegatable {
    
    void delegate(@NotNull Cancellable cancellable, @NotNull DelegateType delegateType);
    
    void cancelDelegates(@NotNull Predicate<DelegateCancellable> filter);
    
}
