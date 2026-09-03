package me.hapyl.hariant.game;

import me.hapyl.eterna.module.component.ComponentList;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.game.battleground.Battleground;
import me.hapyl.hariant.game.type.GameType;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.profile.ui.PlayerUIFormatter;
import me.hapyl.hariant.team.EnumTeam;
import me.hapyl.hariant.team.TeamData;
import me.hapyl.hariant.team.TeamDataMap;
import me.hapyl.hariant.util.HexId;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface GameInstance extends Ticking, PlayerUIFormatter, GameInstanceListener {
    
    @NotNull
    GameInstanceState getState();
    
    void setState(@NotNull GameInstanceState state);
    
    @NotNull
    TeamDataMap getTeamData();
    
    @NotNull
    TeamData getTeamData(@NotNull EnumTeam team);
    
    @NotNull
    GameType getType();
    
    @NotNull
    Battleground getBattleground();
    
    @NotNull
    HexId getId();
    
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
    
    @Override
    void tick();
    
    int getTimeLeft();
    
    @Override
    void formatScoreboard(@NotNull PlayerProfile profile, @NotNull ComponentList components);
    
    @ApiStatus.NonExtendable
    default boolean endIfWinConditionMet() {
        return Hariant.endCurrentGameInstanceIfWinConditionMet();
    }
}