package me.hapyl.hariant.experience;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.component.ButtonComponents;
import me.hapyl.eterna.module.component.ComponentStyler;
import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.component.ProgressBar;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.inventory.menu.ChestSize;
import me.hapyl.eterna.module.inventory.menu.action.PlayerMenuAction;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.inventory.adder.AdderError;
import me.hapyl.hariant.menu.Menu;
import me.hapyl.hariant.menu.MenuPlayerProfile;
import me.hapyl.hariant.menu.MenuReturn;
import me.hapyl.hariant.reward.Reward;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class MenuLevelling extends Menu {
    
    private static final Material[][] LEVEL_ICONS = {
            { Material.YELLOW_STAINED_GLASS_PANE, Material.GOLD_BLOCK },
            { Material.LIME_STAINED_GLASS_PANE, Material.EMERALD_BLOCK },
            { Material.RED_STAINED_GLASS_PANE, Material.REDSTONE_BLOCK }
    };
    
    private static final int[] LEVEL_SLOTS = { 19, 20, 21, 22, 23 };
    private static final int MAX_REWARDS_DISPLAY = 10;
    
    private static final ProgressBar PROGRESS_BAR_BUILDER = new ProgressBar("|", 40, Style.style(Colors.EXPERIENCE));
    private static final ComponentStyler PRESTIGE_STYLER = ComponentStyler.builder(Style.style(Colors.GRAY)).withPadding(1).build();
    
    private final LevelEntry levelEntry;
    private final int currentLevel;
    
    public MenuLevelling(@NotNull Player player) {
        super(player, () -> Component.text("Levelling"), ChestSize.SIZE_5);
        
        this.levelEntry = profile.getDatabase().level;
        this.currentLevel = levelEntry.getLevel();
        
        this.openMenu();
    }
    
    @Override
    public @NotNull MenuReturn menuReturn() {
        return MenuReturn.create(Component.text("Player Profile"), MenuPlayerProfile::new);
    }
    
    @Override
    public void updateMenu() {
        final @NotNull Level[] levelFeed = levelEntry.createLevelFeed(currentLevel);
        
        for (int i = 0; i < levelFeed.length; i++) {
            this.setItem(LEVEL_SLOTS[i], this.createItem(currentLevel, levelFeed[i]));
        }
        
        // Set claim rewards button
        final List<? extends Reward> unclaimedRewards = levelEntry.getUnclaimedRewards();
        
        setItem(
                25,
                this.createItemClaimRewards(unclaimedRewards),
                PlayerMenuAction.of(player -> {
                    if (unclaimedRewards.isEmpty()) {
                        profile.messageError(Component.text("Nothing to claim!"));
                    }
                    else {
                        final int rewardsToClaim = unclaimedRewards.size();
                        final Map<Reward, AdderError> unclaimedReward = Maps.newLinkedHashMap();
                        
                        profile.messageInfo(Component.text("Claiming %s rewards...".formatted(unclaimedRewards.size())));
                        
                        for (Reward reward : unclaimedRewards) {
                            reward.claim(profile, (_, _, error) -> unclaimedReward.put(reward, error));
                        }
                        
                        if (unclaimedReward.isEmpty()) {
                            profile.messageSuccess(Component.text("Successfully claimed all rewards!"));
                        }
                        else {
                            profile.messageSuccess(Component.text("Partially claimed %s/%s rewards!".formatted(rewardsToClaim - unclaimedReward.size(), rewardsToClaim)));
                            profile.messageInfo(Component.text("Here's a list of rewards that couldn't be claimed:"));
                            
                            unclaimedReward.forEach((reward, error) -> profile.messageInfo(
                                    Component.space()
                                             .append(Component.text(reward.getKeyAsString(), Colors.GRAY))
                                             .append(Component.text(": ", Colors.GRAY))
                                             .append(error.asComponent().color(Colors.RED))
                            ));
                        }
                    }
                    
                    this.openMenu();
                })
        );
    }
    
    private @NotNull ItemStack createItem(int currentLevel, @NotNull Level level) {
        final int levelValue = level.getLevel();
        final boolean prestige = level.isPrestige();
        
        final ItemBuilder builder;
        
        // Next level
        if (currentLevel + 1 == levelValue) {
            final long experienceForCurrentLevel = Level.experienceForLevel(currentLevel);
            final long experienceForNextLevel = Level.experienceForLevel(levelValue);
            
            final long currentExperienceScaled = levelEntry.getExperience() - experienceForCurrentLevel;
            final long nextLevelExperienceScaled = experienceForNextLevel - experienceForCurrentLevel;
            
            builder = new ItemBuilder(LEVEL_ICONS[0][prestige ? 1 : 0]);
            
            builder.setName(Component.text("Level " + levelValue, Colors.YELLOW));
            builder.addLore(Component.text("Next Level", Colors.DARK_GRAY));
            
            builder.addLore();
            builder.addLore(
                    Component.empty()
                             .append(Component.text("Progress: ", Colors.GRAY))
                             .append(Components.makeComponentFractional(
                                     currentExperienceScaled,
                                     nextLevelExperienceScaled
                             ))
            );
            builder.addLore(
                    Component.empty()
                             .append(PROGRESS_BAR_BUILDER.build(currentExperienceScaled, nextLevelExperienceScaled))
                             .append(Component.text(" %.2f%%".formatted((double) currentExperienceScaled / nextLevelExperienceScaled * 100), Colors.DARK_GRAY))
            );
        }
        // Passed level
        else if (currentLevel >= levelValue) {
            builder = new ItemBuilder(LEVEL_ICONS[1][prestige ? 1 : 0]);
            
            builder.setName(Component.text("Level " + levelValue, Colors.GREEN));
            builder.addLore(Component.text("Reached Level", Colors.DARK_GRAY));
        }
        // Future level
        else {
            builder = new ItemBuilder(LEVEL_ICONS[2][prestige ? 1 : 0]);
            
            builder.setName(Component.text("Level " + levelValue, Colors.RED));
            builder.addLore(Component.text("Future Level", Colors.DARK_GRAY));
        }
        
        // Append common info
        if (prestige) {
            builder.addLore();
            builder.addLore(Component.text("Prestige Level!", Colors.GOLD));
            builder.addWrappedLore(
                    Component.empty()
                             .append(Component.text("Unlocks a new "))
                             .append(Component.text("color", level.getStyle()))
                             .append(Component.text(" and has greater rewards!")),
                    PRESTIGE_STYLER
            );
        }
        
        builder.addLore();
        builder.addLore(Component.text("Rewards:", Colors.GOLD));
        
        for (Reward reward : level.getRewards()) {
            builder.addLore(
                    Component.space()
                             .append(reward.getName())
                             .appendSpace()
                             .append(Components.checkmark(reward.hasClaimed(profile)))
            );
        }
        
        builder.setAmount(levelValue);
        
        return builder.asIcon();
    }
    
    private @NotNull ItemStack createItemClaimRewards(@NotNull List<? extends Reward> unclaimedRewards) {
        final boolean hasUnclaimedRewards = !unclaimedRewards.isEmpty();
        final ItemBuilder builder = new ItemBuilder(hasUnclaimedRewards ? Material.CHEST_MINECART : Material.MINECART);
        
        builder.setName(Component.text("Claim Rewards"));
        builder.addLore();
        
        if (hasUnclaimedRewards) {
            builder.addLore(Component.text("Rewards to Claim:"));
            
            int count = 0;
            
            for (Reward reward : unclaimedRewards) {
                if (count++ >= MAX_REWARDS_DISPLAY) {
                    builder.addLore(Component.text("...and %s more!".formatted(unclaimedRewards.size() - MAX_REWARDS_DISPLAY), Colors.DARK_GRAY));
                    break;
                }
                
                builder.addLore(Component.space().append(reward.getName()));
            }
            
            builder.addLore();
            builder.addLore(ButtonComponents.left("claim"));
        }
        else {
            builder.addLore(Component.text("Nothing to claim!"));
        }
        
        return builder.asIcon();
    }
    
}