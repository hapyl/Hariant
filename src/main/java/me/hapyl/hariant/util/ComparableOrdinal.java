package me.hapyl.hariant.util;

import org.jetbrains.annotations.NotNull;

public interface ComparableOrdinal<E extends ComparableOrdinal<E>> {
    
    int ordinal();
    
    default boolean isHigher(@NotNull E that) {
        return this.ordinal() > that.ordinal();
    }
    
    default boolean isOrHigher(@NotNull E that) {
        return this.ordinal() >= that.ordinal();
    }
    
    default boolean isLower(@NotNull E that) {
        return this.ordinal() < that.ordinal();
    }
    
    default boolean isOrLower(@NotNull E that) {
        return this.ordinal() <= that.ordinal();
    }
    
}
