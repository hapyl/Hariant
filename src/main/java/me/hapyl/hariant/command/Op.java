package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.hariant.database.rank.PlayerRank;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Op extends TabCompleter {
    
    void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank);
    
    @Override
    @NotNull
    List<String> tabComplete(@NotNull ArgumentList args);
}
