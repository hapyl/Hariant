package me.hapyl.hariant.achievement;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

public record AchievementCategoryInfo(@NotNull List<? extends Achievement> achievements, long totalAmountOfRubies) {
    
    public int totalNumberOfAchievements() {
        return achievements.size();
    }
    
    public @NotNull Stream<? extends Achievement> stream() {
        return achievements.stream();
    }
    
    static @NotNull AchievementCategoryInfo create(@NotNull List<Achievement> achievements) {
        return new AchievementCategoryInfo(achievements, achievements.stream().mapToLong(Achievement::getRubyReward).sum());
    }
    
}
