package me.hapyl.hariant.event;

import net.kyori.adventure.text.Component;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CancellableWithReason extends Cancellable {
    
    @Nullable Cancel getCancel();
    
    void setCancel(@NotNull Cancel cancel);
    
    default @NotNull Component getCancelReason() {
        final Cancel cancel = this.getCancel();
        
        return cancel != null ? cancel.asComponent() : Cancel.cancelDefault().asComponent();
    }
    
    @Override
    default void setCancelled(boolean cancel) {
        this.setCancel(Cancel.cancelDefault());
    }
    
    @Override
    default boolean isCancelled() {
        return this.getCancel() != null;
    }
    
}