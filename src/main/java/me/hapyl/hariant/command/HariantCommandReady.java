package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.profile.PlayerProfile;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HariantCommandReady extends HariantPlayerCommand {
    
    public HariantCommandReady(@NotNull String name) {
        super(name, PlayerRank.DEFAULT);
        
        this.setCooldownSeconds(2);
    }
    
    @Override
    public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        final PlayerProfile profile = Hariant.getPlayerProfile(player);
        
        profile.setReady(!profile.isReady());
    }
    
}
