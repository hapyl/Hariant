package me.hapyl.hariant.achievement;

import me.hapyl.hariant.entity.player.HariantPlayer;
import org.jetbrains.annotations.NotNull;

public class SharedAchievementData {
    
    public final HariantPlayer player;
    
    public SharedAchievementData(@NotNull HariantPlayer player) {
        this.player = player;
    }
    
    public @NotNull HariantPlayer getPlayer() {
        return player;
    }
    
}
