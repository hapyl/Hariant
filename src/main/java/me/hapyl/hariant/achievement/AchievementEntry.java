package me.hapyl.hariant.achievement;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.PlayerDatabaseEntry;
import me.hapyl.hariant.database.problem.Problem;
import me.hapyl.hariant.database.problem.ProblemReporter;
import me.hapyl.hariant.database.serialize.MongoSerializableConstructor;
import me.hapyl.hariant.menu.Menus;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.profile.notification.Notification;
import me.hapyl.hariant.profile.notification.NotificationListener;
import me.hapyl.hariant.profile.notification.NotificationType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class AchievementEntry extends PlayerDatabaseEntry {
    
    public static final NotificationListener NOTIFICATION_LISTENER = new NotificationListener() {
        private final Notification notification = new Notification() {
            @Override
            public @NotNull Component getName() {
                return Component.text("Unclaimed Achievement Rewards");
            }
            
            @Override
            public @NotNull NotificationType getNotificationType() {
                return NotificationType.NORMAL;
            }
            
            @Override
            public @NotNull ClickEvent<?> clickEvent() {
                return Menus.ACHIEVEMENTS.createClickEvent();
            }
        };
        
        @Override
        public @Nullable Notification listen(@NotNull PlayerProfile profile) {
            if (profile.getDatabase().achievements.countUnclaimedRewards() > 0) {
                return notification;
            }
            
            return null;
        }
    };
    
    private final Map<Key, AchievementProgress> achievementProgressMap;
    private boolean hideCompletedAchievements;
    
    @MongoSerializableConstructor
    private AchievementEntry(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull String parent) {
        super(database, document, parent);
        
        this.achievementProgressMap = Maps.newHashMap();
    }
    
    public boolean isHideCompletedAchievements() {
        return hideCompletedAchievements;
    }
    
    public void setHideCompletedAchievements(boolean hideCompletedAchievements) {
        this.hideCompletedAchievements = hideCompletedAchievements;
    }
    
    @Override
    public void write(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        // Write progress
        document.put(
                "progress",
                achievementProgressMap.values()
                                      .stream()
                                      .collect(Collectors.toMap(progress -> progress.getAchievement().getKeyAsString(), progress -> progress.writeToNewDocument(database, problemReporter)))
        );
        
        // Write settings
        document.put("hide_completed_achievements", hideCompletedAchievements);
    }
    
    @Override
    public void read(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        // Read progress
        if (document.get("progress") instanceof Document progressDocument) {
            progressDocument.keySet().forEach(stringKey -> {
                final Document dataDocument = progressDocument.get(stringKey, new Document());
                final Key key = Key.ofStringOrNull(stringKey);
                
                if (key == null) {
                    problemReporter.report(Problem.severe(AchievementEntry.class, "Malformed key: `%s`!".formatted(stringKey)));
                    return;
                }
                
                final Achievement achievement = AchievementRegistry.getRegistry().get(key).orElse(null);
                
                if (achievement == null) {
                    problemReporter.report(Problem.severe(AchievementEntry.class, "Achievement with key `%s` doesn't exist!".formatted(stringKey)));
                    return;
                }
                
                achievementProgressMap.put(key, AchievementProgress.fromDocument(achievement, database, dataDocument, problemReporter));
            });
        }
        
        // Read settings
        this.hideCompletedAchievements = document.get("hide_completed_achievements", false);
    }
    
    public @NotNull Optional<AchievementProgress> getProgress(@NotNull Achievement achievement) {
        return Optional.ofNullable(achievementProgressMap.get(achievement.getKey()));
    }
    
    public @NotNull AchievementProgress getOrCreateProgress(@NotNull Achievement achievement) {
        return achievementProgressMap.computeIfAbsent(achievement.getKey(), _key -> new AchievementProgress(achievement, database));
    }
    
    public boolean hasCompleted(@NotNull Achievement achievement) {
        final AchievementProgress progress = achievementProgressMap.get(achievement.getKey());
        
        return progress != null && progress.hasCompleted();
    }
    
    public boolean hasClaimedRewards(@NotNull Achievement achievement) {
        final AchievementProgress progress = achievementProgressMap.get(achievement.getKey());
        
        return progress != null && progress.hasClaimedRewards();
    }
    
    public boolean hasCompletedClaimedRewards(@NotNull Achievement achievement) {
        return this.hasCompleted(achievement) && this.hasClaimedRewards(achievement);
    }
    
    public boolean hasCompletedNotClaimedRewards(@NotNull Achievement achievement) {
        return this.hasCompleted(achievement) && !this.hasClaimedRewards(achievement);
    }
    
    public @NotNull AchievementProgress progress(@NotNull Achievement achievement, double progress) {
        final AchievementProgress achievementProgress = this.getOrCreateProgress(achievement);
        final Player player = database.getProfile().getPlayer();
        
        // Only progress achievements for online players
        achievementProgress.incrementProgress(player, progress);
        
        return achievementProgress;
    }
    
    public boolean resetProgress(@NotNull Achievement achievement) {
        return achievementProgressMap.remove(achievement.getKey()) != null;
    }
    
    public boolean hasProgress(@NotNull Achievement achievement) {
        return achievementProgressMap.containsKey(achievement.getKey());
    }
    
    public int countUnclaimedRewards() {
        return (int) achievementProgressMap.values().stream().filter(achievement -> achievement.hasCompleted() && !achievement.hasClaimedRewards()).count();
    }
    
    public int countCompletedAchievements() {
        return (int) achievementProgressMap.values().stream().filter(AchievementProgress::hasCompleted).count();
    }
    
    public @NotNull PlayerAchievementCategoryInfo getCategoryInfo(@NotNull AchievementCategory category) {
        final AchievementCategoryInfo categoryInfo = AchievementRegistry.getCategoryInfo(category);
        
        if (categoryInfo == null) {
            return PlayerAchievementCategoryInfo.empty();
        }
        
        long completedAchievements = 0;
        long completedRubies = 0;
        long unclaimedRewards = 0;
        
        for (Achievement achievement : categoryInfo.achievements()) {
            if (this.hasCompleted(achievement)) {
                completedAchievements++;
                
                if (this.hasClaimedRewards(achievement)) {
                    completedRubies += achievement.getRubyReward();
                }
                else {
                    unclaimedRewards++;
                }
            }
        }
        
        return new PlayerAchievementCategoryInfo(categoryInfo.totalNumberOfAchievements(), categoryInfo.totalAmountOfRubies(), completedAchievements, completedRubies, unclaimedRewards);
    }
    
}