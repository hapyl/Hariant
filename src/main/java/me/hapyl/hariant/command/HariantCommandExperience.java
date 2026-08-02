package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.util.StringList;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.experience.Level;
import me.hapyl.hariant.experience.LevelEntry;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HariantCommandExperience extends HariantPlayerCommand {
    
    public HariantCommandExperience(@NotNull String name) {
        super(name, PlayerRank.ADMIN);
    }
    
    @Override
    public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        final Player target = args.get(0).toPlayer();
        
        if (target == null) {
            HariantLogger.error(player, Component.text("Invalid player!"));
            return;
        }
        
        final Operation operation = args.get(1).toEnum(Operation.class);
        
        if (operation == null) {
            HariantLogger.error(player, Component.text("Invalid operation!"));
            return;
        }
        
        final PlayerProfile profile = Hariant.getPlayerProfile(target);
        
        operation.execute(player, profile, profile.getDatabase().level, args.copyOfRange(2));
    }
    
    @Override
    public @NotNull List<String> tabComplete(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        if (args.length == 1) {
            return StringList.ofOnlinePlayers();
        }
        else if (args.get(1).toEnum(Operation.class) instanceof Operation operation) {
            return operation.tabComplete(args.copyOfRange(2));
        }
        
        return StringList.ofEnumConstantLowercaseNames(Operation.class);
    }
    
    private static void addOrRemoveExperience(@NotNull Player player, @NotNull PlayerProfile target, @NotNull LevelEntry entry, @NotNull ArgumentList args, boolean add) {
        // exp (player) (add) (amount)
        final int amountToAdd = args.get(0).toInt();
        
        if (amountToAdd < 0) {
            throw new IllegalArgumentException("Amount cannot be negative!");
        }
        
        final LevelEntry.Result result = entry.addExperience(target, amountToAdd, false);
        
        HariantLogger.success(
                player,
                Component.empty()
                         .append(Component.text("Successfully %s %d experience %s ".formatted(
                                 add ? "added" : "removed",
                                 amountToAdd,
                                 add ? "to" : "from")
                         ))
                         .append(target.getName())
                         .append(Component.text("!  "))
                         .append(Component.text("(New exp %s, %s -> %s)".formatted(result.experienceAfterAdd(), result.levelBeforeAdd().getLevel(), result.levelAfterAdd().getLevel()), Colors.DARK_GRAY))
        );
    }
    
    public enum Operation {
        
        PLAY_LEVEL_UP_FX {
            @Override
            public void execute(@NotNull Player player, @NotNull PlayerProfile target, @NotNull LevelEntry entry, @NotNull ArgumentList args) {
                final int levelFrom = args.get(0).toInt(1);
                final int levelTo = args.get(1).toInt(2);
                
                if (levelFrom >= levelTo) {
                    throw new IllegalArgumentException("levelFrom >= levelTo");
                }
                
                if (levelFrom < HariantConstants.MIN_LEVEL) {
                    throw new IllegalArgumentException("levelFrom < " + HariantConstants.MIN_LEVEL);
                }
                
                if (levelTo > HariantConstants.MAX_LEVEL) {
                    throw new IllegalArgumentException("levelTo > " + HariantConstants.MAX_LEVEL);
                }
                
                entry.notifyLevelUp(player, Level.forLevel(levelFrom), Level.forLevel(levelTo));
            }
        },
        
        ADD {
            @Override
            public void execute(@NotNull Player player, @NotNull PlayerProfile target, @NotNull LevelEntry entry, @NotNull ArgumentList args) {
                addOrRemoveExperience(player, target, entry, args, true);
            }
        },
        
        REMOVE {
            @Override
            public void execute(@NotNull Player player, @NotNull PlayerProfile target, @NotNull LevelEntry entry, @NotNull ArgumentList args) {
                addOrRemoveExperience(player, target, entry, args, false);
            }
        },
        
        RESET {
            @Override
            public void execute(@NotNull Player player, @NotNull PlayerProfile target, @NotNull LevelEntry entry, @NotNull ArgumentList args) {
                entry.reset();
                
                HariantLogger.success(player, Component.text("Successfully reset experience for ").append(target.getName()).append(Component.text("!")));
            }
        };
        
        public void execute(@NotNull Player player, @NotNull PlayerProfile target, @NotNull LevelEntry entry, @NotNull ArgumentList args) {
        }
        
        public @NotNull List<String> tabComplete(@NotNull ArgumentList args) {
            return List.of();
        }
        
    }
    
}
