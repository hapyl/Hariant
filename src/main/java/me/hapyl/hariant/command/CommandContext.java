package me.hapyl.hariant.command;

import me.hapyl.eterna.module.util.TypeConverter;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.entity.player.HariantPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface CommandContext {
    
    @NotNull Player getPlayer();
    
    @NotNull HariantPlayer getHariantPlayer() throws IllegalStateException;
    
    @NotNull TypeConverter get(int index);
    
    @NotNull PlayerRank getPlayerRank();
    
}
