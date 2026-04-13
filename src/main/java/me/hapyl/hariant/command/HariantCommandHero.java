package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.menu.hero.MenuHeroSelection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HariantCommandHero extends HariantPlayerCommand {
    HariantCommandHero(@NotNull String name) {
        super(name, PlayerRank.DEFAULT);
    }
    
    @Override
    protected void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        new MenuHeroSelection(player);
    }
}
