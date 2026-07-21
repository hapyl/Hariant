package me.hapyl.hariant.command;

import me.hapyl.eterna.module.util.TypeConverter;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.profile.PlayerProfile;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface CommandContext {
    
    @NotNull Player getPlayer();
    
    @NotNull HariantPlayer getHariantPlayer() throws IllegalStateException;
    
    @NotNull TypeConverter argument(int index);
    
    int argumentLength();
    
    @NotNull PlayerRank getPlayerRank();
    
    default @NotNull PlayerProfile getProfile() {
        return Hariant.getPlayerProfile(this.getPlayer());
    }
    
}
