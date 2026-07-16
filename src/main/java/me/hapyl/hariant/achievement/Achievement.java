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
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface Achievement extends Keyed, Named, Described, Registrable, ItemCreator {
    
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
    
    @Override
    void onRegister();
    
    @Override
    void onUnregister();
    
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
    
}