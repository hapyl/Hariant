package me.hapyl.hariant.dialog;

import me.hapyl.eterna.module.player.dialog.Dialog;
import me.hapyl.eterna.module.player.dialog.DialogEndType;
import me.hapyl.eterna.module.player.dialog.entry.DialogEntry;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.profile.setting.Settings;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class HariantDialog extends Dialog {
    
    private final boolean isRepeatable;
    
    public HariantDialog(@NotNull Key key, @NotNull Component name, boolean isRepeatable) {
        super(key, name);
        
        this.isRepeatable = isRepeatable;
    }
    
    public boolean isRepeatable() {
        return isRepeatable;
    }
    
    @Override
    public void onDialogEnd(@NotNull Player player, @NotNull DialogEndType dialogEndType) {
        super.onDialogEnd(player, dialogEndType);
        
        // Don't save if player left or the dialog is repeatable
        if (dialogEndType == DialogEndType.PLAYER_LEFT || isRepeatable()) {
            return;
        }
        
        complete(player);
    }
    
    public boolean hasCompleted(@NotNull Player player) {
        return Hariant.getPlayerDatabase(player).dialog.dialogsCompleted.containsKey(getKey());
    }
    
    public void complete(@NotNull Player player) {
        Hariant.getPlayerDatabase(player).dialog.dialogsCompleted.put(getKey(), System.currentTimeMillis());
    }
    
    @Override
    @Range(from = 1L, to = 2147483647L)
    public int getEntryDelay(@NotNull Player player, @NotNull DialogEntry entry) {
        final DialogSpeed dialogSpeed = Hariant.getPlayerDatabase(player).settings.getValue(Settings.DIALOG_SPEED);
        
        return (int) (entry.getDelay() * dialogSpeed.getMultiplier());
    }
}
