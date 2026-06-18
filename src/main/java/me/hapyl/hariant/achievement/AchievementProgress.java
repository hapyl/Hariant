package me.hapyl.hariant.achievement;

import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.problem.ProblemReporter;
import me.hapyl.hariant.database.serialize.MongoSerializable;
import me.hapyl.hariant.database.serialize.codec.MongoCodecs;
import me.hapyl.hariant.inventory.item.ResourceRegistry;
import me.hapyl.hariant.util.Timestamp;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AchievementProgress implements MongoSerializable {
    
    private final Achievement achievement;
    private final PlayerDatabase playerDatabase;
    
    private double progress;
    
    private @Nullable Timestamp completedAt;
    private @Nullable Timestamp rewardsClaimedAt;
    
    AchievementProgress(@NotNull Achievement achievement, @NotNull PlayerDatabase playerDatabase) {
        this.achievement = achievement;
        this.playerDatabase = playerDatabase;
        this.completedAt = null;
        this.rewardsClaimedAt = null;
    }
    
    public @NotNull Achievement getAchievement() {
        return achievement;
    }
    
    public double getProgress() {
        return progress;
    }
    
    public boolean hasCompleted() {
        return completedAt != null;
    }
    
    public boolean hasClaimedRewards() {
        return rewardsClaimedAt != null;
    }
    
    public @Nullable Timestamp getCompletedAt() {
        return completedAt;
    }
    
    public @Nullable Timestamp getRewardsClaimedAt() {
        return rewardsClaimedAt;
    }
    
    public boolean incrementProgress(@NotNull Player player, double progress) {
        final double goal = achievement.getGoal();
        
        // If current progress is higher than goal and progress is capped, return
        if (this.progress >= goal && achievement.isProgressCapped()) {
            return true;
        }
        
        // Increment progress
        return this.setProgress(player, this.progress + progress);
    }
    
    public boolean setProgress(@NotNull Player player, double progress) {
        final double progressBefore = this.progress;
        final double goal = this.achievement.getGoal();
        
        this.progress = Math.max(0, progress);
        this.achievement.onProgress(player, this, progressBefore, this.progress);
        
        // If progress was less than goal and now higher than goal, the achievement is complete
        if (progressBefore < goal && this.progress >= goal) {
            this.achievement.onComplete(player, this);
            this.completedAt = Timestamp.ofNow();
            return true;
        }
        
        return false;
    }
    
    public boolean claimRewards(@NotNull Player player) {
        if (this.hasClaimedRewards()) {
            return false;
        }
        
        this.rewardsClaimedAt = Timestamp.ofNow();
        this.playerDatabase.inventory.addResource(ResourceRegistry.RUBY, achievement.getTier().getRubyReward());
        this.achievement.onRewardsClaimed(player, this);
        return true;
    }
    
    @Override
    public void write(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        // Write progress
        document.put("progress", progress);
        
        // Write timestamps
        MongoCodecs.TIMESTAMP.writeNullable(document, "completed_at", completedAt);
        MongoCodecs.TIMESTAMP.writeNullable(document, "rewards_claimed_at", rewardsClaimedAt);
    }
    
    @Override
    public void read(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        // Read progress
        this.progress = document.get("progress", 0.0);
        
        // Read timestamps
        this.completedAt = MongoCodecs.TIMESTAMP.read(document, "completed_at").orElse(null);
        this.rewardsClaimedAt = MongoCodecs.TIMESTAMP.read(document, "rewards_claimed_at").orElse(null);
    }
    
    static @NotNull AchievementProgress fromDocument(@NotNull Achievement achievement, @NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        final AchievementProgress data = new AchievementProgress(achievement, database);
        data.read(database, document, problemReporter);
        
        return data;
    }
    
}