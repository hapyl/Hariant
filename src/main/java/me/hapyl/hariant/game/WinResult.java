package me.hapyl.hariant.game;

import me.hapyl.hariant.team.EnumTeam;
import me.hapyl.hariant.team.TeamEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public class WinResult {
    
    private final WinType winType;
    private final List<EnumTeam> winningTeams;
    
    WinResult(@NotNull WinType winType, @NotNull List<EnumTeam> winningTeams) {
        this.winType = winType;
        this.winningTeams = List.copyOf(winningTeams);
    }
    
    @NotNull
    public WinType getWinType() {
        return winType;
    }
    
    @NotNull
    @Unmodifiable
    public List<EnumTeam> getWinningTeams() {
        return winningTeams;
    }
    
    public boolean isWinner(@NotNull TeamEntry entry) {
        for (EnumTeam winningTeam : winningTeams) {
            if (winningTeam.isInTeam(entry)) {
                return true;
            }
        }
        
        return false;
    }
    
    @NotNull
    public static WinResult create(@NotNull WinType winType, @NotNull List<EnumTeam> winningTeams) {
        return new WinResult(winType, winningTeams);
    }
    
    public static WinResult create(@NotNull WinType winType, @NotNull EnumTeam winningTeam) {
        return new WinResult(winType, List.of(winningTeam));
    }
    
    @Nullable
    public static WinResult notWon() {
        return null;
    }
    
}
