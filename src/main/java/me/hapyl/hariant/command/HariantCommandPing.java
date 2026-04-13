package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.rank.PlayerRank;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class HariantCommandPing extends HariantPlayerCommand {
    
    public HariantCommandPing(@NotNull String name) {
        super(name, PlayerRank.DEFAULT);
    }
    
    @Override
    protected void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        final Player target = Objects.requireNonNullElse(args.get(0).toPlayer(), player);
        final Component targetName = Hariant.getPlayerProfile(target).getNameFormatted();
        
        HariantLogger.system(
                player,
                Component.empty()
                         .append(targetName)
                         .append(Component.text("'s ping is "))
                         .append(Component.text(target.getPing()))
                         .append(Component.text("."))
        );
    }
    
}
