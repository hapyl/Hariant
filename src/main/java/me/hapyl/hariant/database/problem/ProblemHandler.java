package me.hapyl.hariant.database.problem;

import org.jetbrains.annotations.NotNull;

public interface ProblemHandler {
    
    void handle(@NotNull Problem problem);
    
}
