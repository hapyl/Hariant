package me.hapyl.hariant.game.battleground.feature;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.game.GameInstance;
import me.hapyl.hariant.game.GameInstanceListener;
import me.hapyl.hariant.game.WinResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface BattlegroundFeature extends Named, Described, Ticking, GameInstanceListener {
    
    @Override
    @NotNull Component getName();
    
    @Override
    @NotNull Component getDescription();
    
    @Override
    void tick();
    
    @Override
    void onCreate(@NotNull Iterable<? extends HariantPlayer> players);
    
    @Override
    void onDestroy(@NotNull Iterable<? extends HariantPlayer> players, @NotNull WinResult result);
    
    @Override
    void onFinalize(@NotNull List<? extends HariantPlayer> players, @NotNull WinResult result);
    
    @Override
    void onKill(@NotNull GameInstance gameInstance, @NotNull HariantPlayer player, @NotNull HariantPlayer victim);
    
    @Override
    void onDeath(@NotNull GameInstance gameInstance, @NotNull HariantPlayer player, @Nullable HariantEntity killer);
    
}