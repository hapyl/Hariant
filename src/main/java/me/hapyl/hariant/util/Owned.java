package me.hapyl.hariant.util;

import org.jetbrains.annotations.Nullable;

public interface Owned<T> {
    
    @Nullable
    T getOwner();
    
    void setOwner(@Nullable T owner);
    
    default boolean isOwned() {
        return getOwner() != null;
    }
    
}
