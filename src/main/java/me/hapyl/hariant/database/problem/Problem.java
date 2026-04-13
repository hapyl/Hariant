package me.hapyl.hariant.database.problem;

import me.hapyl.eterna.module.util.ComparableTo;
import org.jetbrains.annotations.NotNull;

public final class Problem implements ComparableTo<Problem> {
    
    private final ProblemType problemType;
    private final Class<?> problemClass;
    private final String problem;
    
    Problem(@NotNull ProblemType problemType, @NotNull Class<?> problemClass, @NotNull String problem) {
        this.problemType = problemType;
        this.problemClass = problemClass;
        this.problem = problem;
    }
    
    @NotNull
    public Class<?> getProblemClass() {
        return problemClass;
    }
    
    @NotNull
    public ProblemType getProblemType() {
        return problemType;
    }
    
    @NotNull
    public String getProblem() {
        return problem;
    }
    
    @Override
    public int compareTo(@NotNull Problem that) {
        return this.problemType.compareTo(that.problemType);
    }
    
    @Override
    public String toString() {
        return "%s Problem @ %s -> %s".formatted(problemType.name(), problemClass.getSimpleName(), problem);
    }
    
    @NotNull
    public static Problem warning(@NotNull Class<?> problemClass, @NotNull String problem) {
        return new Problem(ProblemType.WARNING, problemClass, problem);
    }
    
    @NotNull
    public static Problem severe(@NotNull Class<?> problemClass, @NotNull String problem) {
        return new Problem(ProblemType.SEVERE, problemClass, problem);
    }
    
}
