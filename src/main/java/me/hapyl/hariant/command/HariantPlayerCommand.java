package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.entity.player.HariantPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public abstract class HariantPlayerCommand extends HariantCommand {
    
    public HariantPlayerCommand(@NotNull String name, @NotNull PlayerRank rank) {
        super(name, rank);
    }
    
    protected abstract void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank);
    
    @Override
    protected final void execute(@NotNull CommandSender sender, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        if (!(sender instanceof Player player)) {
            HariantLogger.error(sender, Component.text("You must be a player to perform this command!"));
            return;
        }
        
        this.execute(player, args, playerRank);
    }
    
    public void asHariantPlayer(@NotNull Player player, @NotNull Consumer<HariantPlayer> consumer) {
        Hariant.getPlayer(player).ifPresentOrElse(consumer, () -> HariantLogger.error(player, Component.text("You must have a player handle to execute this command!")));
    }
    
}
