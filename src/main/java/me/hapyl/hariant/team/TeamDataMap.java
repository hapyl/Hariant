package me.hapyl.hariant.team;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.Streamable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Stream;

public final class TeamDataMap implements Streamable<TeamData> {
    
    private final Map<EnumTeam, TeamData> enumMap;
    
    public TeamDataMap() {
        this.enumMap = Maps.newEnumMap(EnumTeam.class);
        
        // Populate non-empty teams with a team data
        EnumTeam.getPopulatedTeams().forEach(team -> enumMap.put(team, team.createTeamData()));
    }
    
    @NotNull
    public TeamData getData(@NotNull EnumTeam team) {
        return enumMap.computeIfAbsent(team, EnumTeam::createTeamData);
    }
    
    @NotNull
    @Override
    public Stream<TeamData> stream() {
        return enumMap.values().stream();
    }
    
}
