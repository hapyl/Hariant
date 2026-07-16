package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.util.StringList;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.rank.PlayerRank;
import me.hapyl.hariant.game.battleground.EnumBattleground;
import me.hapyl.hariant.menu.MenuBattlegroundSelection;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class HariantCommandBattleground extends HariantPlayerCommand {
    
    private static final StringList BATTLEGROUNDS = StringList.ofEnumConstantLowercaseNames(EnumBattleground.class);
    
    public HariantCommandBattleground(@NotNull String name) {
        super(name, PlayerRank.DEFAULT);
    }
    
    @Override
    public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        if (args.length == 0) {
            new MenuBattlegroundSelection(player);
        }
        else {
            final EnumBattleground battleground = args.get(0).toEnum(EnumBattleground.class);
            
            if (battleground == null) {
                HariantLogger.error(player, Component.text("Invalid battleground!"));
                return;
            }
            
            if (battleground.isSelected()) {
                HariantLogger.error(player, Component.text("This battleground is already selected!"));
                return;
            }
            
            battleground.select();
            HariantLogger.success(player, Component.text("Selected battleground: ").append(battleground.getName()));
        }
    }
    
    @Override
    public @NotNull List<String> tabComplete(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        if (args.length == 1) {
            return BATTLEGROUNDS;
        }
        
        return List.of();
    }
}
