package me.hapyl.hariant.debug;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.hariant.entity.player.HariantPlayer;
import org.jetbrains.annotations.NotNull;

public interface Debug {
    
    void debug(@NotNull HariantPlayer player, @NotNull ArgumentList args);
    
}
