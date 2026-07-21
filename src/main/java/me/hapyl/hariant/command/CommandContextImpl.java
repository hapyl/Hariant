package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.util.TypeConverter;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.entity.player.HariantPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandContextImpl implements CommandContext {
    
    private final Player player;
    private final ArgumentList args;
    private final PlayerRank playerRank;
    
    CommandContextImpl(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        this.player = player;
        this.args = args;
        this.playerRank = playerRank;
    }
    
    @Override
    public @NotNull Player getPlayer() {
        return player;
    }
    
    @Override
    public @NotNull HariantPlayer getHariantPlayer() {
        return Hariant.getPlayer(player).orElseThrow(() -> new IllegalArgumentException("You must have a player instance to execute this command!"));
    }
    
    @Override
    public @NotNull TypeConverter argument(int index) {
        return args.get(index);
    }
    
    @Override
    public int argumentLength() {
        return args.length;
    }
    
    @Override
    public @NotNull PlayerRank getPlayerRank() {
        return playerRank;
    }
}
