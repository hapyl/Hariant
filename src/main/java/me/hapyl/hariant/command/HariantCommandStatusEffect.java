package me.hapyl.hariant.command;

import com.google.common.collect.ImmutableList;
import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.util.StringList;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.entity.effect.status.StatusEffectType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HariantCommandStatusEffect extends HariantPlayerCommand {
    
    private static final List<String> COMPLETIONS = ImmutableList.<String>builder()
                                                                 .addAll(StringList.ofEnumConstantLowercaseNames(StatusEffectType.class))
                                                                 .add("clear")
                                                                 .build();
    
    public HariantCommandStatusEffect(@NotNull String name) {
        super(name, PlayerRank.ADMIN);
    }
    
    @Override
    public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        final HariantPlayer hariantPlayer = Hariant.getPlayer(player).orElse(null);
        
        if (hariantPlayer == null) {
            HariantLogger.error(player, Component.text("You must have a player instance to execute this command!"));
            return;
        }
        
        // Clear
        if (args.get(0).toString().equalsIgnoreCase("clear")) {
            final int clearedEffects = hariantPlayer.clearEffects();
            
            if (clearedEffects == 0) {
                hariantPlayer.messageError(Component.text("You don't have any status effects!"));
            }
            else {
                hariantPlayer.messageSuccess(Component.text("Cleared %s status effects!".formatted(clearedEffects)));
            }
            
            return;
        }
        
        final StatusEffectType statusEffect = args.get(0).toEnum(StatusEffectType.class);
        final int duration = args.get(1).toInt();
        
        if (statusEffect == null) {
            HariantLogger.error(player, Component.text("Invalid status effect!"));
            return;
        }
        
        if (duration < 0) {
            HariantLogger.error(player, Component.text("Duration must be positive!"));
            return;
        }
        
        if (duration == 0) {
            hariantPlayer.removeEffect(statusEffect);
            hariantPlayer.messageSuccess(Component.text("Removed ").append(statusEffect.getName()).append(Component.text(" effect!")));
        }
        else {
            hariantPlayer.addEffect(statusEffect, duration, hariantPlayer);
            hariantPlayer.messageSuccess(
                    Component.empty()
                             .append(Component.text("Added "))
                             .append(statusEffect.getName())
                             .append(Component.text(" effect for "))
                             .append(Component.text(Tick.format(duration)))
                             .append(Component.text("."))
            );
        }
    }
    
    @Override
    public @NotNull List<String> tabComplete(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        if (args.length == 1) {
            return COMPLETIONS;
        }
        
        return List.of();
    }
    
}