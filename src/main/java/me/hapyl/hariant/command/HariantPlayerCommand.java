package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.rank.PlayerRank;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class HariantPlayerCommand extends HariantCommand {
    
    public HariantPlayerCommand(@NotNull String name, @NotNull PlayerRank rank) {
        super(name, rank);
    }
    
    public abstract void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank);
    
    @NotNull
    public List<String> tabComplete(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        return List.of();
    }
    
    @Override
    public final void execute(@NotNull CommandSender sender, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        if (!(sender instanceof Player player)) {
            HariantLogger.error(sender, Component.text("You must be a player to perform this command!"));
            return;
        }
        
        this.execute(player, args, playerRank);
    }
    
    @NotNull
    @Override
    public final List<String> tabComplete(@NotNull CommandSender sender, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        if (sender instanceof Player player) {
            return this.tabComplete(player, args, playerRank);
        }
        
        return List.of();
    }
    
}
