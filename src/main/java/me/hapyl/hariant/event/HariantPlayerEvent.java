package me.hapyl.hariant.event;

import me.hapyl.hariant.entity.player.HariantPlayer;
import org.jetbrains.annotations.NotNull;

public abstract class HariantPlayerEvent extends HariantEvent {
    
    private final HariantPlayer player;
    
    public HariantPlayerEvent(@NotNull HariantPlayer player) {
        this.player = player;
    }
    
    @NotNull
    public HariantPlayer getPlayer() {
        return player;
    }
    
}
