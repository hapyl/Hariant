package me.hapyl.hariant.game;

import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface GameInstanceListener {
    
    void onCreate(@NotNull Iterable<? extends HariantPlayer> players);
    
    void onDestroy(@NotNull Iterable<? extends HariantPlayer> players, @NotNull WinResult result);
    
    void onFinalize(@NotNull List<? extends HariantPlayer> players, @NotNull WinResult result);
    
    void onKill(@NotNull GameInstance gameInstance, @NotNull HariantPlayer player, @NotNull HariantPlayer victim);
    
    void onDeath(@NotNull GameInstance gameInstance, @NotNull HariantPlayer player, @Nullable HariantEntity source);
    
}
