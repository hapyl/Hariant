package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.command.completer.CompleterHandler;
import me.hapyl.eterna.module.util.Enums;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.debug.EnumDebug;
import me.hapyl.hariant.entity.player.HariantPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class HariantDebugCommand extends HariantCommand {
    public HariantDebugCommand(@NotNull String name) {
        super(name, PlayerRank.ADMIN);
        
        setCompleterHandler(
                CompleterHandler.builder(0)
                                .values(Arrays.stream(EnumDebug.values()).map(Enum::name).map(String::toLowerCase).toList())
                                .build()
        );
    }
    
    @Override
    protected void execute(@NotNull CommandSender sender, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        if (!(sender instanceof Player bukkitPlayer)) {
            HariantLogger.error(sender, Component.text("You must be a player to execute this command!", NamedTextColor.RED));
            return;
        }
        
        final HariantPlayer player = Hariant.getPlayer(bukkitPlayer).orElse(null);
        
        if (player == null) {
            HariantLogger.error(sender, Component.text("You must have a player instance to execute this command!"));
            return;
        }
        
        final EnumDebug debug = Enums.byName(EnumDebug.class, args.get(0).toString());
        
        if (debug == null) {
            player.messageError(Component.text("Invalid debug!"));
            return;
        }
        
        player.sendMessage(Component.text("Executing %s debug...".formatted(debug), NamedTextColor.YELLOW));
        
        debug.debug(player, ArgumentList.copyOfRange(args, 1, args.length));
    }
}
