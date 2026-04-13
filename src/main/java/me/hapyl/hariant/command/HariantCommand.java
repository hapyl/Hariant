package me.hapyl.hariant.command;

import me.hapyl.eterna.EternaColors;
import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.command.SimpleCommand;
import me.hapyl.hariant.database.rank.PlayerRank;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class HariantCommand extends SimpleCommand {
    
    private final PlayerRank rank;
    
    public HariantCommand(@NotNull String name, @NotNull PlayerRank rank) {
        super(name);
        
        this.rank = rank;
    }
    
    protected abstract void execute(@NotNull CommandSender sender, @NotNull ArgumentList args, @NotNull PlayerRank playerRank);
    
    @Override
    protected final void execute(@NotNull CommandSender sender, @NotNull ArgumentList args) {
        final PlayerRank playerRank = PlayerRank.getRank(sender);
        
        if (!playerRank.isOrHigher(rank)) {
            sender.sendMessage(Component.text("You must be %s or higher to perform this command!".formatted(rank.name().toLowerCase()), EternaColors.DARK_RED));
            return;
        }
        
        this.execute(sender, args, playerRank);
    }
}
