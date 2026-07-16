package me.hapyl.hariant.menu.achievement;

import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.PlayerMenuTitle;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.achievement.*;
import me.hapyl.hariant.inventory.item.ResourceRegistry;
import me.hapyl.hariant.menu.Menu;
import me.hapyl.hariant.menu.MenuPage;
import me.hapyl.hariant.menu.MenuPlayerProfile;
import me.hapyl.hariant.util.Timestamp;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Comparator;

public class MenuAchievement extends MenuPage<Achievement> {
    
    private final AchievementEntry achievementEntry;
    private final AchievementCategory category;
    
    public MenuAchievement(@NotNull Player player, @NotNull AchievementCategory category) {
        super(player, PlayerMenuTitle.create(Component.text("Achievements"), category.getName()));
        
        this.achievementEntry = profile.getDatabase().achievements;
        
        this.category = category;
        
        this.setContents(
                AchievementRegistry.getByCategory(category)
                                   // Only show achievements if they're not hidden, unless progressed
                                   .filter(achievement -> {
                                       if (achievement.isHidden()) {
                                           return achievementEntry.hasProgress(achievement);
                                       }
                                       
                                       return true;
                                   })
                                   // Sort the achievements
                                   .sorted(Comparator.comparingInt(achievement -> {
                                       final AchievementProgress progress = achievementEntry.getProgress(achievement).orElse(null);
                                       
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
                                   }))
                                   .toList()
        );
        
        this.openMenu();
    }
    
    @Override
    public @NotNull Component returnMenuName() {
        return Component.text("Player Profile");
    }
    
    @Override
    public @NotNull Menu returnMenu(@NotNull Player player) {
        return new MenuPlayerProfile(player);
    }
    
    @Override
    public @NotNull ItemBuilder createBuilder(@NotNull Achievement achievement) {
        final ItemBuilder builder = achievement.createBuilder();
        
        final AchievementProgress progress = achievementEntry.getProgress(achievement).orElse(null);
        final AchievementTier achievementTier = achievement.getTier();
        
        // If achievement is completed, show the completion time
        if (progress != null) {
            final Timestamp completedAt = progress.getCompletedAt();
            
            builder.addLore();
            
            if (completedAt != null) {
                if (!progress.hasClaimedRewards()) {
                    builder.glow();
                    builder.addLore(Component.text("REWARDS UNCLAIMED!", Colors.GOLD, TextDecoration.BOLD));
                    builder.addLore(Component.space().append(ResourceRegistry.RUBY.format(achievementTier.getRubyReward())));
                    
                    builder.addLore();
                    builder.addLore(ButtonComponents.left("claim"));
                }
                else {
                    builder.addLore(Component.text("COMPLETED!", Colors.GREEN, TextDecoration.BOLD));
                    builder.addLore(Component.space().append(completedAt.asComponent().color(Colors.GRAY)));
                }
            }
            // Show progress
            else {
                builder.addLore(Component.text("PROGRESS", Colors.YELLOW, TextDecoration.BOLD));
                builder.addLore(Component.space().append(Components.makeComponentFractional(progress.getProgress(), achievement.getGoal())));
            }
        }
        
        return builder;
    }
    
    @Override
    public void onClick(@NotNull Achievement achievement, @NotNull ClickType clickType) {
        // Only button is to claim the rewards
        final AchievementProgress progress = achievementEntry.getProgress(achievement).orElse(null);
        
        if (progress != null && progress.hasCompletedButNotClaimedRewards()) {
            if (progress.claimRewards(player)) {
                player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 3, 2.0f);
                player.playSound(player, Sound.ENTITY_VILLAGER_YES, 3, 2.0f);
            }
            
            openMenu();
        }
    }
    
    @Override
    public void openMenu(@Range(from = 1, to = Integer.MAX_VALUE) int page) {
        super.openMenu(page);
        
        // Set category items
        for (AchievementCategory category : AchievementCategory.values()) {
            final ItemBuilder builder = category.createBuilder();
            final boolean current = this.category == category;
            
            builder.setName(category.getName().color(current ? Colors.GREEN : Colors.RED));
            builder.addLore();
            
            builder.addWrappedLore(category.getDescription());
            builder.addLore();
            
            builder.addLore(current ? Component.text("Currently selected!", Colors.GREEN) : ButtonComponents.left("select"));
            
            if (current) {
                setItem(category.getSlot(), builder.asIcon());
            }
            else {
                setItem(category.getSlot(), builder.asIcon(), PlayerMenuAction.of(player -> new MenuAchievement(player, category)));
            }
            
        }
    }
    
}
