package me.hapyl.hariant.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.IntFunction;

@ThisClassShouldNeMovedToEternaAPI
public class ImmutableCollectionMerger {
    
    private ImmutableCollectionMerger() {
    }
    
    public static <E> @NotNull Set<? extends E> merge(@NotNull Set<? extends E> original, @NotNull Set<? extends E> toMerge) {
        if (original.isEmpty()) {
            return toMerge;
        }
        
        return Set.copyOf(merge0(original, toMerge, HashSet::new));
    }
    
    public static <E> @NotNull List<? extends E> merge(@NotNull List<? extends E> original, @NotNull List<? extends E> toMerge) {
        if (original.isEmpty()) {
            return toMerge;
        }
        
        return List.copyOf(merge0(original, toMerge, ArrayList::new));
    }
    
    private static <E, C extends Collection<E>> @NotNull C merge0(@NotNull Collection<? extends E> original, @NotNull Collection<? extends E> toMerge, @NotNull IntFunction<C> factory) {
        final C merged = factory.apply(original.size() + toMerge.size());
        
        merged.addAll(original);
        merged.addAll(toMerge);
        
        return merged;
    }
    
}
