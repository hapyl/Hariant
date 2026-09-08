package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.util.StringList;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.util.BoundingBoxCreator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HariantCommandBoundingBoxCreator extends HariantPlayerCommand {
    
    private static final String ARGUMENT = "coordinates";
    
    public HariantCommandBoundingBoxCreator(@NotNull String name) {
        super(name, PlayerRank.ADMIN);
    }
    
    @Override
    public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        if (args.get(0).toString().equalsIgnoreCase(ARGUMENT)) {
            BoundingBoxCreator.coordinates(player);
        }
        else {
            BoundingBoxCreator.toggle(player);
        }
        
    }
    
    @Override
    public @NotNull List<String> tabComplete(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        if (args.length == 1) {
            return StringList.of(ARGUMENT);
        }
        
        return List.of();
    }
}
