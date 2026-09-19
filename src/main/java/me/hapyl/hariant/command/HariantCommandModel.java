package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.util.StringList;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.util.Models;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HariantCommandModel extends HariantPlayerCommand {
    
    public HariantCommandModel(@NotNull String name) {
        super(name, PlayerRank.ADMIN);
    }
    
    @Override
    public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        final Models model = args.get(0).toEnum(Models.class);
        
        if (model == null) {
            HariantLogger.error(player, Component.text("Unknown model!"));
            return;
        }
        
        model.spawn(player.getLocation());
        
        HariantLogger.success(player, Component.text("Spawned `%s` model!".formatted(model.name().toLowerCase())));
    }
    
    @Override
    public @NotNull List<String> tabComplete(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        if (args.length == 1) {
            return StringList.ofEnumConstantLowercaseNames(Models.class);
        }
        
        return List.of();
    }
    
}