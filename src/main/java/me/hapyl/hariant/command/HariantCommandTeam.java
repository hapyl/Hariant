package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.team.MenuTeamSelection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class HariantCommandTeam extends HariantPlayerCommand {
    
    public HariantCommandTeam(@NotNull String name) {
        super(name, PlayerRank.DEFAULT);
    }
    
    @Override
    public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        new MenuTeamSelection(player);
    }
}