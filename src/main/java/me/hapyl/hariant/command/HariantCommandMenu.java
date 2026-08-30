package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.util.StringList;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.menu.Menus;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HariantCommandMenu extends HariantPlayerCommand {
    
    public static final String COMMAND_NAME = "menu";
    
    public HariantCommandMenu(@NotNull String name) {
        super(name, PlayerRank.DEFAULT);
    }
    
    @Override
    public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        final Menus menus = args.get(0).toEnum(Menus.class);
        
        if (menus == null) {
            HariantLogger.error(player, Component.text("Unknown menu: `%s`!".formatted(args.get(0))));
            return;
        }
        
        menus.openMenu(player);
    }
    
    @Override
    public @NotNull List<String> tabComplete(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        if (args.length == 1) {
            return StringList.ofEnumConstantLowercaseNames(Menus.class);
        }
        
        return List.of();
    }
    
}