package me.hapyl.hariant.statistics;

import me.hapyl.hariant.talent.Talent;
import org.jetbrains.annotations.NotNull;

public interface Statistics {
    
    void incrementStatistic(@NotNull Statistic statistic, double value);
    
    void incrementTalentUsage(@NotNull Talent talent);
    
    double getStatistic(@NotNull Statistic statistic);
    
    int getTalentUsage(@NotNull Talent talent);
    
}
