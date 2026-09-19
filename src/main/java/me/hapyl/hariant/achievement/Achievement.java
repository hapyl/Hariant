package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.hariant.inventory.item.ItemCreator;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.registry.Registrable;
import me.hapyl.hariant.util.RewardsRubies;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface Achievement extends Keyed, Named, Described, Registrable, ItemCreator, RewardsRubies {
    
    @Override
    @NotNull Key getKey();
    
    @Override
    @NotNull Component getName();
    
    @Override
    @NotNull Component getDescription();
    
    @Override
    @NotNull ItemBuilder createBuilder();
    
    double getGoal();
    
    @NotNull AchievementCategory getCategory();
    
    @NotNull AchievementTier getTier();
    
    @Nullable Achievement getParent();
    
    default @NotNull Set<? extends Component> getSearchComponents() {
        return Set.of(this.getName());
    }
    
    @Override
    default int getRubyReward() {
        return this.getTier().getRubyReward();
    }
    
    @Override
    void onRegister();
    
    boolean isHidden();
    
    boolean isProgressCapped();
    
    @EventLike
    void onProgress(@NotNull Player player, @NotNull AchievementProgress achievementProgress, double progressBefore, double progress);
    
    @EventLike
    void onComplete(@NotNull Player player, @NotNull AchievementProgress achievementProgress);
    
    @EventLike
    void onRewardsClaimed(@NotNull Player player, @NotNull AchievementProgress achievementProgress);
    
    default void progress(@NotNull PlayerProfile profile, double progress) {
        profile.getDatabase().achievements.progress(this, progress);
    }
    
    default void progress(@NotNull PlayerProfile profile) {
        this.progress(profile, 1);
    }
    
    /**
     * Progress this achievement as well as all parents of this achievement for the given profile.
     *
     * @param profile  - The profile to progress for.
     * @param progress - The achievement progress.
     * @implNote Note that this method must be called on the youngest member of the family!
     */
    default void progressFamily(@NotNull PlayerProfile profile, double progress) {
        this.progress(profile, progress);
        
        if (this.getParent() instanceof Achievement parent) {
            parent.progressFamily(profile, progress);
        }
    }
    
    default void progressFamily(@NotNull PlayerProfile profile) {
        this.progressFamily(profile, 1);
    }
    
    default boolean filter(@NotNull AchievementEntry achievementEntry) {
        // If achievement is complete and rewards are claimed and settings is set to hide, then hide
        if (achievementEntry.hasCompletedClaimedRewards(this) && achievementEntry.isHideCompletedAchievements()) {
            return false;
        }
        
        // If the achievement has a parent, only show it if the parent is completed
        // FIXME (xanyjl @ Sunday, August 16) -> Check whether parent rewards are claimed before showing?
        final Achievement parent = this.getParent();
        
        if (parent != null && !achievementEntry.hasCompleted(parent)) {
            return false;
        }
        
        // Only display hidden achievements if they have been completed
        if (this.isHidden()) {
            return achievementEntry.hasProgress(this);
        }
        
        return true;
    }
    
    default int sorted(@NotNull AchievementEntry achievementEntry) {
        final AchievementProgress progress = achievementEntry.getProgress(this).orElse(null);
        
        if (progress != null) {
            if (progress.hasCompleted()) {
                // If rewards are unclaimed, display first, otherwise, display last
                return progress.hasClaimedRewards() ? 3 : 0;
            }
            
            // If achievement has progress but not yet complete, display second
            return 1;
        }
        
        // Default to displaying third
        return 2;
    }
    
    @NotNull AchievementUniqueIdCounter createUniqueIdCounter(@NotNull PlayerProfile profile);
    
    int uniqueIdCounterValue();
    
}