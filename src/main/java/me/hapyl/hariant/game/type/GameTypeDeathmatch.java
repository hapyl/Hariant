package me.hapyl.hariant.game.type;

import me.hapyl.eterna.module.component.ComponentList;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.game.GameInstance;
import me.hapyl.hariant.game.WinResult;
import me.hapyl.hariant.game.WinType;
import me.hapyl.hariant.profile.PlayerProfile;
import me.hapyl.hariant.team.EnumTeam;
import me.hapyl.hariant.team.TeamData;
import me.hapyl.hariant.team.TeamDataMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class GameTypeDeathmatch extends GameTypeImpl {
    
    private static final int TOP_TEAMS_LIMIT = 5;
    
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
                                 .append(team.getFirstLetterFormatted())
                                 .appendSpace()
                                 .append(team.formatPlayerNames())
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
    
    @Nullable
    @Override
    public WinResult checkWinCondition(@NotNull GameInstance gameInstance) {
        final EnumTeam winningTeam = gameInstance.getTeamData()
                                                 .stream()
                                                 .filter(teamData -> teamData.kills >= killGoal)
                                                 .findFirst()
                                                 .map(TeamData::getTeam)
                                                 .orElse(null);
        
        return winningTeam != null ? WinResult.create(WinType.WIN_CONDITION_MET, winningTeam) : WinResult.notWon();
    }
    
    @Override
    public void onDeath(@NotNull GameInstance gameInstance, @NotNull HariantPlayer player) {
        final boolean isWinConditionMet = gameInstance.endIfWinConditionMet();
        
        if (!isWinConditionMet) {
            player.respawn(respawnTime);
        }
    }
    
    @Override
    public void onKill(@NotNull GameInstance gameInstance, @NotNull HariantPlayer player, @NotNull HariantPlayer victim) {
    }
    
    @Override
    @NotNull
    public List<EnumTeam> getWiningTeamsWhenTimeLimit(@NotNull GameInstance gameInstance) {
        return gameInstance.getTeamData()
                           .stream()
                           .collect(Collectors.groupingBy(TeamData::getTeam, Collectors.summingInt(TeamData::getKills)))
                           .entrySet()
                           .stream()
                           .max(Map.Entry.comparingByValue())
                           .map(Map.Entry::getKey)
                           .stream()
                           .toList();
    }
    
}