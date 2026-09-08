package me.hapyl.hariant.game.type;

import me.hapyl.eterna.module.component.ComponentList;
import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.game.GameInstance;
import me.hapyl.hariant.game.GameInstanceListener;
import me.hapyl.hariant.game.Placement;
import me.hapyl.hariant.game.WinResult;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.team.EnumTeam;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface GameType extends Named, Described, GameInstanceListener {
    
    @Override
    @NotNull Component getName();
    
    @Override
    @NotNull Component getDescription();
    
    int getMinimumTeamsRequired();
    
    int getTimeLimit();
    
    boolean allowDuplicateHeroes();
    
    void formatScoreboard(@NotNull PlayerProfile profile, @NotNull GameInstance gameInstance, @NotNull ComponentList components);
    
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
    
    boolean checkWinCondition(@NotNull GameInstance gameInstance);
    
    @NotNull Map<EnumTeam, Placement> getWinningTeams(@NotNull GameInstance gameInstance);
}
