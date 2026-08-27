package me.hapyl.hariant.menu.achievement;

import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.PlayerMenuTitle;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.achievement.*;
import me.hapyl.hariant.inventory.item.ResourceRegistry;
import me.hapyl.hariant.menu.MenuPage;
import me.hapyl.hariant.menu.MenuPlayerProfile;
import me.hapyl.hariant.menu.MenuReturn;
import me.hapyl.hariant.util.Timestamp;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Comparator;

public class MenuAchievement extends MenuPage<Achievement> {
    
    private static final ItemStack ITEM_NO_CONTENTS = new ItemBuilder(Material.MINECART)
            .setName(Component.text("No Achievements!", Colors.RED))
            .addWrappedLore(
                    Component.empty()
                             .appendNewline()
                             .append(Component.text("There aren't any achievements in this category!"))
            )
            .asIcon();
    
    private static final ItemStack ITEM_NO_CONTENTS_FILTERING = new ItemBuilder(Material.COMMAND_BLOCK_MINECART)
            .setName(Component.text("No Matching Achievements!", Colors.RED))
            .addWrappedLore(
                    Component.empty()
                             .appendNewline()
                             .append(Component.text("There aren't any completed achievements with unclaimed rewards, nice!"))
            )
            .asIcon();
    
    private final AchievementEntry achievementEntry;
    private final AchievementCategory category;
    
    public MenuAchievement(@NotNull Player player, @NotNull AchievementCategory category) {
        super(player, PlayerMenuTitle.create(Component.text("Achievements"), category.getName()));
        
        this.achievementEntry = profile.getDatabase().achievements;
        this.category = category;
        
        this.updateContentsOpenMenu();
    }
    
    public MenuAchievement(@NotNull Player player) {
        this(player, AchievementCategory.GENESIS);
    }
    
    @Override
    public @NotNull MenuReturn menuReturn() {
        return MenuReturn.create(Component.text("Player Profile"), MenuPlayerProfile::new);
    }
    
    @Override
    public @NotNull ItemBuilder createBuilder(@NotNull Achievement achievement) {
        final ItemBuilder builder = achievement.createBuilder();
        
        final AchievementProgress progress = achievementEntry.getProgress(achievement).orElse(null);
        final AchievementTier achievementTier = achievement.getTier();
        final double achievementGoal = achievement.getGoal();
        
        // Change the name based on whether the achievement is complete or not
        Style nameStyle = Style.style(Colors.RED).decoration(TextDecoration.ITALIC, false);
        
        builder.addLore();
        
        // If achievement is completed, show the completion time
        if (progress != null) {
            final Timestamp completedAt = progress.getCompletedAt();
            
            if (completedAt != null) {
                if (!progress.hasClaimedRewards()) {
                    builder.glow();
                    builder.addLore(Component.text("REWARDS UNCLAIMED!", Colors.GOLD, TextDecoration.BOLD));
                    builder.addLore(Component.space().append(ResourceRegistry.RUBY.format(achievementTier.getRubyReward())));
                    
                    builder.addLore();
                    builder.addLore(ButtonComponents.left("claim"));
                }
                else {
                    // If achievement has uncapped progress, show total progress
                    if (!achievement.isProgressCapped()) {
                        builder.addLore(Component.text("Total ").append(Component.text("%,.0f".formatted(progress.getProgress()), Colors.NUMBER)));
                        builder.addLore();
                    }
                    
                    builder.addLore(Component.text("COMPLETED!", Colors.GREEN, TextDecoration.BOLD));
                    builder.addLore(Component.space().append(completedAt.asComponent().color(Colors.DARK_GRAY)));
                }
                
                nameStyle = nameStyle.color(Colors.GREEN);
            }
            // Show progress
            else {
                setProgress(builder, progress.getProgress(), achievementGoal);
                
                nameStyle = nameStyle.color(Colors.YELLOW);
            }
        }
        // If progress doesn't exist, we "fake" it being 0/N
        else {
            setProgress(builder, 0, achievementGoal);
        }
        
        // Edit name color based on the progress
        builder.setName(achievement.getName().style(nameStyle));
        
        return builder;
    }
    
    @Override
    public void onClick(@NotNull Achievement achievement, @NotNull ClickType clickType) {
        // Only button is to claim the rewards
        final AchievementProgress progress = achievementEntry.getProgress(achievement).orElse(null);
        
        if (progress != null && progress.hasCompleted() && !progress.hasClaimedRewards()) {
            if (progress.claimRewards(player)) {
                player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 3, 2.0f);
                player.playSound(player, Sound.ENTITY_VILLAGER_YES, 3, 2.0f);
            }
            
            this.updateContentsOpenMenu();
        }
    }
    
    @Override
    public void openMenu(@Range(from = 1, to = Integer.MAX_VALUE) int page) {
        super.openMenu(page);
        
        int slot = 1;
        
        // Set category items
        for (AchievementCategory category : AchievementCategory.values()) {
            // Count unclaimed rewards for the current category
            final PlayerAchievementCategoryInfo categoryInfo = achievementEntry.getCategoryInfo(category);
            
            final ItemBuilder builder = category.createBuilder();
            final boolean current = this.category == category;
            
            builder.setName(category.getName().color(current ? Colors.GREEN : Colors.RED));
            builder.addLore();
            
            // Append description
            builder.addWrappedLore(category.getDescription());
            builder.addLore();
            
            // Append total number of completed achievements
            builder.addLore(
                    Component.empty()
                             .append(Component.text("Achievements: "))
                             .append(Components.makeComponentFractional(categoryInfo.completedAchievements(), categoryInfo.totalNumberOfAchievements()))
            );
            
            builder.addLore(
                    Component.empty()
                             .append(Component.text("Rubies: "))
                             .append(Components.makeComponentFractional(categoryInfo.completedRubies(), categoryInfo.totalAmountOfRubies()))
            );
            
            builder.addLore();
            
            // If player has unclaimed rewards, show via item amount and append lore
            final int numberOfUnclaimedRewards = (int) categoryInfo.numberOfUnclaimedRewards();
            
            if (numberOfUnclaimedRewards > 0) {
                builder.setAmount(numberOfUnclaimedRewards);
                
                builder.addLore(Component.text("%s unclaimed rewards!".formatted(numberOfUnclaimedRewards), Colors.GREEN, TextDecoration.UNDERLINED));
                builder.addLore();
            }
            
            if (current) {
                builder.glow();
                builder.addLore(Component.text("Currently selected!", Colors.GREEN));
                
                setItem(slot, builder.asIcon(), PlayerMenuAction.of(player -> {
                    HariantLogger.error(player, Component.text("Already here!"));
                    HariantLogger.sound(player, Sound.ENTITY_VILLAGER_NO, 1.0f);
                }));
            }
            else {
                builder.addLore(ButtonComponents.left("select"));
                
                setItem(slot, builder.asIcon(), PlayerMenuAction.of(player -> {
                    new MenuAchievement(player, category);
                }));
            }
            
            slot = Math.min(slot + 1, 7);
        }
        
        // Set button
        final boolean hideCompletedAchievements = achievementEntry.isHideCompletedAchievements();
        
        setFooter(
                2,
                new ItemBuilder(hideCompletedAchievements ? Material.LIME_DYE : Material.GRAY_DYE)
                        .setName(Component.text("Hide Completed Achievements", hideCompletedAchievements ? Colors.GREEN : Colors.RED))
                        .addLore()
                        .addWrappedLore(Component.text("Whether to hide completed achievements once rewards are claimed."))
                        .addLore()
                        .addLore(ButtonComponents.left(hideCompletedAchievements ? "disable" : "enable"))
                        .asIcon(),
                PlayerMenuAction.of(player -> {
                    achievementEntry.setHideCompletedAchievements(!hideCompletedAchievements);
                    HariantLogger.sound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
                    
                    this.updateContentsOpenMenu();
                })
        );
    }
    
    @Override
    public @NotNull ItemStack getItemNoContents() {
        return achievementEntry.isHideCompletedAchievements() ? ITEM_NO_CONTENTS_FILTERING : ITEM_NO_CONTENTS;
    }
    
    private void updateContentsOpenMenu() {
        this.setContents(
                AchievementRegistry.streamCategory(category)
                                   .filter(achievement -> achievement.filter(achievementEntry))
                                   .sorted(Comparator.comparingInt(achievement -> achievement.sorted(achievementEntry)))
                                   .toList()
        );
        
        this.openMenu();
    }
    
    private static void setProgress(@NotNull ItemBuilder builder, double progress, double goal) {
        builder.addLore(Component.text("PROGRESS", Colors.YELLOW, TextDecoration.BOLD));
        builder.addLore(Component.space().append(Components.makeComponentFractional(progress, goal)));
    }
    
}
