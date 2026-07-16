package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.player.dialog.Dialog;
import me.hapyl.eterna.module.player.dialog.DialogInstance;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.rank.PlayerRank;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HariantCommandSkipDialog extends HariantPlayerCommand {
    
    public HariantCommandSkipDialog(@NotNull String name) {
        super(name, PlayerRank.DEFAULT);
        
        setCooldownSeconds(10);
    }
    
    @Override
    public void execute(@NotNull Player player, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        final DialogInstance dialogInstance = Dialog.getCurrentDialog(player).orElse(null);
        
        if (dialogInstance == null) {
            HariantLogger.error(player, Component.text("You aren't currently in dialog!"));
            return;
        }
        
        dialogInstance.skip();
    }
    
}
