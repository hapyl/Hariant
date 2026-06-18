package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.registry.Registrable;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface Achievement extends Keyed, Named, Described, Registrable {
    
    @NotNull Component HIDDEN_ACHIEVEMENT_NAME = Component.text("???", Colors.ERROR);
    
    @Override
    @NotNull Key getKey();
    
    @Override
    @NotNull Component getName();
    
    @Override
    @NotNull Component getDescription();
    
    double getGoal();
    
    @NotNull AchievementCategory getCategory();
    
    @NotNull AchievementTier getTier();
    
    @Override
    void onRegister();
    
    @Override
    void onUnregister();
    
    boolean isHidden();
    
    boolean isProgressCapped();
    
    default @NotNull Component getNameOrQuestionMarks(@NotNull PlayerDatabase playerDatabase) {
        return this.isHidden() && !playerDatabase.achievements.hasCompleted(this)
               ? HIDDEN_ACHIEVEMENT_NAME
               : this.getName();
    }
    
    @EventLike
    void onProgress(@NotNull Player player, @NotNull AchievementProgress achievementProgress, double progressBefore, double progress);
    
    @EventLike
    void onComplete(@NotNull Player player, @NotNull AchievementProgress achievementProgress);
    
    @EventLike
    void onRewardsClaimed(@NotNull Player player, @NotNull AchievementProgress achievementProgress);
}