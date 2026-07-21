package me.hapyl.hariant.entity;

import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.util.Cancellable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class DelegateCancellable implements Cancellable {
    
    private final Cancellable cancellable;
    private final DelegateType delegateType;
    
    DelegateCancellable(@NotNull Cancellable cancellable, @NotNull DelegateType delegateType) {
        this.cancellable = cancellable;
        this.delegateType = delegateType;
    }
    
    public @NotNull DelegateType getDelegateType() {
        return delegateType;
    }
    
    public boolean isInterruptable() {
        return delegateType == DelegateType.INTERRUPTABLE;
    }
    
    public boolean isPersistent() {
        return delegateType == DelegateType.PERSISTENT;
    }
    
    @Override
    public void cancel() {
        this.cancellable.cancel();
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.cancellable);
    }
    
    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final DelegateCancellable that = (DelegateCancellable) object;
        return Objects.equals(this.cancellable, that.cancellable);
    }
    
}