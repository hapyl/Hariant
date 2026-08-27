package me.hapyl.hariant.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public class Counter extends Number implements Comparable<Counter>, ComponentLike {
    
    private int count;
    
    private Counter(int initialCount) {
        this.count = initialCount;
    }
    
    public void increment() {
        ++count;
    }
    
    public void decrement() {
        --count;
    }
    
    public int count() {
        return count;
    }
    
    @Override
    public int intValue() {
        return count;
    }
    
    @Override
    public long longValue() {
        return count;
    }
    
    @Override
    public float floatValue() {
        return count;
    }
    
    @Override
    public double doubleValue() {
        return count;
    }
    
    @Override
    public int compareTo(@NotNull Counter that) {
        return Integer.compare(this.count, that.count);
    }
    
    @Override
    public @NotNull Component asComponent() {
        return Component.text(count);
    }
    
    @Override
    public @NotNull String toString() {
        return String.valueOf(count);
    }
    
    public static @NotNull Counter counter(int initialCount) {
        return new Counter(initialCount);
    }
    
    public static @NotNull Counter counter() {
        return counter(0);
    }
    
}