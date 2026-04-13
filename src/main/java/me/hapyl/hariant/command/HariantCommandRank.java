package me.hapyl.hariant.command;

import me.hapyl.eterna.EternaColors;
import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.util.TypeConverter;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.database.rank.PlayerRank;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HariantCommandRank extends HariantCommand {
    
    public HariantCommandRank(@NotNull String name) {
        super(name, PlayerRank.ADMIN);
    }
    
    @Override
    protected void execute(@NotNull CommandSender sender, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        // rank (target) [rank]
        final TypeConverter argument0 = args.get(0);
        final Player target = argument0.toPlayer();
        
        final TypeConverter argument1 = args.get(1);
        final PlayerRank rankToSet = argument1.toEnum(PlayerRank.class);
        
        // TODO @Feb 08, 2026 (xanyjl) -> Add staff check
        
        if (target == null) {
            sender.sendMessage(Component.text("This player is not online!", Colors.ERROR));
            return;
        }
        
        if (rankToSet == null) {
            // If rank is null, means we're reading it
            final PlayerRank targetRank = PlayerRank.getRank(target);
            
            sender.sendMessage(Component.text("%s's rank is `%s`!".formatted(target.getName(), targetRank.name().toLowerCase()), Colors.SUCCESS));
            return;
        }
        
        Hariant.getPlayerDatabase(target).setRank(rankToSet);
        
        sender.sendMessage(Component.text("Set %s's rank to `%s`!".formatted(argument0, argument1), Colors.SUCCESS));
    }
    
}
