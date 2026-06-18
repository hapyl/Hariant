package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.util.StringList;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.achievement.Achievement;
import me.hapyl.hariant.achievement.AchievementEntry;
import me.hapyl.hariant.achievement.AchievementProgress;
import me.hapyl.hariant.achievement.AchievementRegistry;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.rank.PlayerRank;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class HariantCommandAchievement extends HariantPlayerCommand {
    
    public HariantCommandAchievement(@NotNull String name) {
        super(name, PlayerRank.ADMIN);
    }
    
    @Override
    public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        final Player target = args.get(0).toPlayer();
        final Achievement achievement = args.get(1).toRegistryItem(AchievementRegistry.getRegistry()).orElse(null);
        final Operation operation = args.get(2).toEnum(Operation.class);
        
        if (target == null) {
            HariantLogger.error(player, Component.text("This player is not online!"));
            return;
        }
        
        if (achievement == null) {
            HariantLogger.error(player, Component.text("Achievement with key `%s` doesn't exist!".formatted(args.get(1))));
            return;
        }
        
        if (operation == null) {
            HariantLogger.error(player, Component.text("Invalid operation: %s!".formatted(args.get(2))));
            return;
        }
        
        final PlayerDatabase database = Hariant.getPlayerDatabase(target);
        
        operation.execute(player, target, achievement, database.achievements, args.copyOfRange(3, args.length));
    }
    
    @Override
    public @NotNull List<String> tabComplete(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        if (args.length == 1) {
            return StringList.ofOnlinePlayers();
        }
        else if (args.length == 2) {
            return StringList.ofRegistryKeys(AchievementRegistry.getRegistry());
        }
        else if (args.length == 3) {
            return StringList.ofEnumConstantLowercaseNames(Operation.class);
        }
        
        final Operation operation = args.get(2).toEnum(Operation.class);
        
        return operation != null ? operation.tabComplete(args.copyOfRange(3, args.length)) : StringList.of();
    }
    
    
    private enum Operation implements TabCompleter {
        INFO {
            @Override
            public void execute(@NotNull Player player, @NotNull Player target, @NotNull Achievement achievement, @NotNull AchievementEntry entry, @NotNull ArgumentList args) {
                final AchievementProgress achievementProgress = entry.getProgress(achievement).orElse(null);
                
                if (achievementProgress == null) {
                    HariantLogger.error(
                            player,
                            Component.empty()
                                     .append(target.name())
                                     .append(Component.text(" has not yet progressed this achievement."))
                    );
                    return;
                }
                
                final double progress = achievementProgress.getProgress();
                final double goal = achievement.getGoal();
                
                HariantLogger.success(player, Component.empty().append(target.name()).append(Component.text("'s progress for ")).append(achievement.getName()).append(Component.text(":")));
                
                HariantLogger.info(player, Component.empty().append(Component.text(" Progress: ").append(Components.makeComponentFractional(progress, goal))));
                HariantLogger.info(player, Component.empty().append(Component.text(" Completed: ").append(Components.checkmark(achievementProgress.hasCompleted()))));
                HariantLogger.info(player, Component.empty().append(Component.text(" Rewards Claimed: ").append(Components.checkmark(achievementProgress.hasClaimedRewards()))));
            }
        },
        
        RESET {
            @Override
            public void execute(@NotNull Player player, @NotNull Player target, @NotNull Achievement achievement, @NotNull AchievementEntry entry, @NotNull ArgumentList args) {
                if (entry.resetProgress(achievement)) {
                    HariantLogger.success(player, Component.empty().append(Component.text("Successfully reset achievement progress for ").append(target.name()).append(Component.text("!"))));
                }
                else {
                    HariantLogger.error(
                            player,
                            Component.empty()
                                     .append(Component.text("Could not reset progress for "))
                                     .append(target.name())
                                     .append(Component.text(" because they haven't made any progress yet!"))
                    );
                }
            }
        },
        
        PROGRESS {
            @Override
            public void execute(@NotNull Player player, @NotNull Player target, @NotNull Achievement achievement, @NotNull AchievementEntry entry, @NotNull ArgumentList args) {
                final String argument = args.get(0).toString();
                final double progress = args.get(1).toDouble(0);
                
                if (progress <= 0.0) {
                    HariantLogger.error(player, Component.text("Progress must be positive!"));
                    return;
                }
                
                final AchievementProgress achievementProgress = entry.getOrCreateProgress(achievement);
                
                switch (argument.toLowerCase()) {
                    case "set" -> {
                        achievementProgress.setProgress(target, progress);
                        
                        HariantLogger.success(
                                player,
                                Component.empty()
                                         .append(Component.text("Set "))
                                         .append(target.name())
                                         .append(Component.text("'s achievement progress to "))
                                         .append(Component.text(progress, Colors.NUMBER))
                                         .append(Component.text("!"))
                        );
                    }
                    
                    case "add" -> this.incrementOrDecrement(player, target, achievementProgress, achievement, progress);
                    case "remove" -> this.incrementOrDecrement(player, target, achievementProgress, achievement, -progress);
                }
            }
            
            @Override
            public @NotNull List<String> tabComplete(@NotNull ArgumentList args) {
                return args.length == 1 ? List.of("set", "add", "remove") : List.of();
            }
            
            public void incrementOrDecrement(@NotNull Player player, @NotNull Player target, @NotNull AchievementProgress achievementProgress, @NotNull Achievement achievement, double progress) {
                achievementProgress.incrementProgress(target, progress);
                final double newProgress = achievementProgress.getProgress();
                
                HariantLogger.success(
                        player,
                        Component.empty()
                                 .append(progress > 0 ? Component.text("Incremented ") : Component.text("Decremented"))
                                 .append(target.name())
                                 .append(Component.text("'s achievement progress by "))
                                 .append(Component.text(progress, Colors.NUMBER))
                                 .append(Component.text("; new progress "))
                                 .append(Components.makeComponentFractional(newProgress, achievement.getGoal()))
                                 .append(Component.text("."))
                );
            }
        },
        
        CLAIM_REWARDS {
            @Override
            public void execute(@NotNull Player player, @NotNull Player target, @NotNull Achievement achievement, @NotNull AchievementEntry entry, @NotNull ArgumentList args) {
                final AchievementProgress achievementProgress = entry.getProgress(achievement).orElse(null);
                
                if (achievementProgress == null || !achievementProgress.hasCompleted()) {
                    HariantLogger.error(
                            player,
                            Component.empty()
                                     .append(Component.text("Cannot claim rewards for "))
                                     .append(target.name())
                                     .append(Component.text(" because they haven't completed the achievement!"))
                    );
                    return;
                }
                
                
                if (achievementProgress.claimRewards(target)) {
                    HariantLogger.success(player, Component.empty().append(Component.text("Successfully claimed rewards for ")).append(target.name()).append(Component.text("!")));
                }
                else {
                    HariantLogger.error(player, Component.empty().append(target.name()).append(Component.text(" has already claimed rewards!")));
                }
            }
        };
        
        public void execute(@NotNull Player player, @NotNull Player target, @NotNull Achievement achievement, @NotNull AchievementEntry entry, @NotNull ArgumentList args) {
        }
        
        public @NotNull List<String> tabComplete(@NotNull ArgumentList args) {
            return List.of();
        }
    }
}