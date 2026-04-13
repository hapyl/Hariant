package me.hapyl.hariant.game;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.hariant.entity.player.HariantPlayer;
import org.jetbrains.annotations.NotNull;

public interface PlayerCallback {
    
    @EventLike
    void onKill(@NotNull GameInstance gameInstance, @NotNull HariantPlayer player, @NotNull HariantPlayer victim);
    
    @EventLike
    void onDeath(@NotNull GameInstance gameInstance, @NotNull HariantPlayer player);
    
}
