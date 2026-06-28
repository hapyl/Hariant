package me.hapyl.hariant.debug;

import me.hapyl.hariant.entity.player.HariantPlayer;
import org.jetbrains.annotations.NotNull;

public interface DebugListener {
    
    void onDebugCooldownReset(@NotNull HariantPlayer player);
    
}
