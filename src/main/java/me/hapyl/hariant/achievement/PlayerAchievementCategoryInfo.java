package me.hapyl.hariant.achievement;

import org.jetbrains.annotations.NotNull;

public record PlayerAchievementCategoryInfo(long totalNumberOfAchievements, long totalAmountOfRubies, long completedAchievements, long completedRubies, long numberOfUnclaimedRewards) {
    
    private static final PlayerAchievementCategoryInfo EMPTY = new PlayerAchievementCategoryInfo(0, 0, 0, 0, 0);
    
    public static @NotNull PlayerAchievementCategoryInfo empty() {
        return EMPTY;
    }
    
}