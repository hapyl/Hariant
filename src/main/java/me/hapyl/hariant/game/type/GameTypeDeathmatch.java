package me.hapyl.hariant.game.type;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.component.ComponentList;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.game.GameInstance;
import me.hapyl.hariant.game.Placement;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.team.EnumTeam;
import me.hapyl.hariant.team.TeamData;
import me.hapyl.hariant.team.TeamDataMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class GameTypeDeathmatch extends GameTypeImpl {
    
    private static final int TOP_TEAMS_LIMIT = 5;
    private static final Comparator<TeamData> COMPARATOR = (a, b) -> Integer.compare(b.kills, a.kills);
    
    private final int respawnTime = Tick.fromSeconds(3);
    private final int killGoal = 10;
    
    GameTypeDeathmatch() {
        super(
                Component.text("Deathmatch"),
                Component.text("Deathmatch you fight you kill ok"),
                Tick.fromMinutes(10),
                false
        );
    }
    
    @Override
    public void formatScoreboard(@NotNull PlayerProfile profile, @NotNull GameInstance gameInstance, @NotNull ComponentList components) {
        components.append(Component.text("TOP TEAMS", Colors.GOLD, TextDecoration.BOLD));
        
        final TeamDataMap teamDataMap = gameInstance.getTeamData();
        final List<TeamData> topTeams = teamDataMap.stream().sorted(Comparator.reverseOrder()).limit(TOP_TEAMS_LIMIT).toList();
        
        for (int i = 0; i < TOP_TEAMS_LIMIT; i++) {
            final TextComponent teamNumber = Component.text(" #%s. ".formatted(i + 1), Colors.GRAY);
            
            if (i < topTeams.size()) {
                final TeamData teamData = topTeams.get(i);
                final EnumTeam team = teamData.getTeam();
                
                components.append(
                        Component.empty()
                                 .append(teamNumber)
                                 .append(team.createPlayersComponentWithFirstLetter())
                                 .appendSpace()
                                 .append(Component.text("(", Colors.DARK_GRAY))
                                 .append(Component.text("⚔ ", Colors.RED))
                                 .append(Component.text(teamData.kills))
                                 .append(Component.text(")", Colors.DARK_GRAY))
                                 .append()
                );
            }
            else {
                components.append(teamNumber.append(Component.text("...", Colors.DARK_GRAY)));
            }
        }
    }
    
    @Override
    public void onKill(@NotNull GameInstance gameInstance, @NotNull HariantPlayer player, @NotNull HariantPlayer victim) {
    }
    
    @Override
    public void onDeath(@NotNull GameInstance gameInstance, @NotNull HariantPlayer player) {
        final boolean isWinConditionMet = gameInstance.endIfWinConditionMet();
        
        if (!isWinConditionMet) {
            player.respawn(respawnTime);
        }
    }
    
    @Override
    public boolean checkWinCondition(@NotNull GameInstance gameInstance) {
        return gameInstance.getTeamData().stream().anyMatch(teamData -> teamData.kills >= killGoal);
    }
    
    @Override
    public @NotNull Map<EnumTeam, Placement> getWinningTeams(@NotNull GameInstance gameInstance) {
        final List<TeamData> sortedData = gameInstance.getTeamData().stream().filter(TeamData::hasKills).sorted(COMPARATOR).toList();
        
        // If there aren't any teams with at least one kill, means there aren't any winners ¯\_(ツ)_/¯
        if (sortedData.isEmpty()) {
            return Map.of();
        }
        
        final Map<EnumTeam, Placement> placementMap = Maps.newLinkedHashMap();
        
        int bracket = 0;
        int topKills = sortedData.getFirst().kills;
        
        for (TeamData teamData : sortedData) {
            final int kills = teamData.kills;
            
            // If `topKills` is higher than team's kills, means we moved a bracket,
            // so update `topKills` and increment the bracket
            if (topKills > kills) {
                topKills = kills;
                bracket++;
            }
            
            // Map the team to the bracket, which has a fail-safe for overflow
            placementMap.put(teamData.getTeam(), Placement.placement(bracket));
        }
        
        return placementMap;
    }
    
}