package me.hapyl.hariant.task.executor;

import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;
import java.util.function.Predicate;

public class WhileImpl implements While {
    
    private final Predicate<Integer> predicate;
    private final IntFunction<Boolean> function;
    
    WhileImpl(@NotNull Predicate<Integer> predicate, @NotNull IntFunction<@NotNull Boolean> function) {
        this.predicate = predicate;
        this.function = function;
    }
    
    @Override
    public boolean condition(int tick) {
        return predicate.test(tick);
    }
    
    @Override
    public boolean run(int tick) {
        return function.apply(tick);
    }
}
