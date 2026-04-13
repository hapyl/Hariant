package me.hapyl.hariant.database.problem;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ProblemReporter {
    
    void report(@NotNull Problem problem);
    
    @NotNull
    List<Problem> problems();
    
    @ApiStatus.NonExtendable
    default void handle(@NotNull ProblemHandler problemHandler) {
        final List<Problem> problems = problems();
        
        for (Problem problem : problems) {
            problemHandler.handle(problem);
        }
    }
    
}
