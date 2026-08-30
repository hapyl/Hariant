package me.hapyl.hariant.game;

import me.hapyl.eterna.module.component.Components;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.team.EnumTeam;
import me.hapyl.hariant.team.TeamEntry;
import me.hapyl.hariant.team.TeamEntryProvider;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class WinResult {
    
    private static final List<? extends Component> NO_WINNER_COMPONENTS = List.of(Components.centerText("No winners, yuk", Colors.DARK_GRAY));
    
    private final WinType winType;
    private final Map<? extends EnumTeam, ? extends Placement> teamPlacements;
    
    WinResult(@NotNull WinType winType, @NotNull Map<? extends EnumTeam, ? extends Placement> teamPlacements) {
        this.winType = winType;
        this.teamPlacements = Map.copyOf(teamPlacements);
    }
    
    public @NotNull WinType getWinType() {
        return winType;
    }
    
    public @NotNull Map<? extends EnumTeam, ? extends Placement> getTeamPlacements() {
        return teamPlacements;
    }
    
    public @NotNull Placement getPlacement(@NotNull TeamEntry teamEntry) {
        for (Map.Entry<? extends EnumTeam, ? extends Placement> entry : teamPlacements.entrySet()) {
            if (entry.getKey().isInTeam(teamEntry)) {
                return entry.getValue();
            }
        }
        
        return Placement.PARTICIPATION;
    }
    
    public @NotNull Placement getPlacement(@NotNull TeamEntryProvider teamEntryProvider) {
        return this.getPlacement(teamEntryProvider.teamEntry());
    }
    
    public @NotNull List<? extends Component> createWinnerCenterComponents() {
        if (teamPlacements.isEmpty()) {
            return NO_WINNER_COMPONENTS;
        }
        
        return teamPlacements.entrySet()
                             .stream()
                             .sorted(Map.Entry.comparingByKey())
                             .map(entry -> {
                                 final EnumTeam team = entry.getKey();
                                 final Placement placement = entry.getValue();
                                 
                                 return Components.center(
                                         Component.empty()
                                                  .append(placement.asSmallCaps().style(placement.getStyle()))
                                                  .appendSpace()
                                                  .append(team.createPlayersComponentWithFirstLetter())
                                 );
                             })
                             .toList();
    }
    
    public static @NotNull WinResult create(@NotNull WinType winType, @NotNull Map<? extends EnumTeam, ? extends Placement> teamPlacements) {
        return new WinResult(winType, teamPlacements);
    }
    
    
}
