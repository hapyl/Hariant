package me.hapyl.hariant.entity.player;

import me.hapyl.hariant.util.Cancellable;
import org.jetbrains.annotations.NotNull;

public interface Delegatable {
    
    void delegate(@NotNull Cancellable cancellable, @NotNull DelegateType delegateType);
    
}
