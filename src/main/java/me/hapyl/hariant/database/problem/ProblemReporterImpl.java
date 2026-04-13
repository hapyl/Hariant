package me.hapyl.hariant.database.problem;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProblemReporterImpl implements ProblemReporter {
    
    private final List<Problem> problems;
    
    public ProblemReporterImpl() {
        this.problems = Lists.newArrayList();
    }
    
    @Override
    public void report(@NotNull Problem problem) {
        problems.add(problem);
    }
    
    @Override
    @NotNull
    public List<Problem> problems() {
        return List.copyOf(problems);
    }
    
}
