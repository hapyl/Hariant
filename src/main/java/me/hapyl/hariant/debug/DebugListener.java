package me.hapyl.hariant.debug;

import me.hapyl.hariant.entity.player.HariantPlayer;
import org.jetbrains.annotations.NotNull;

public interface DebugListener {
    
    void debugOnCooldownReset(@NotNull HariantPlayer player);
    
}
