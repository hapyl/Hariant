package me.hapyl.hariant.team;

import org.jetbrains.annotations.NotNull;

public final class TeamData implements Comparable<TeamData> {
    
    private final EnumTeam team;
    
    public int kills;
    public int deaths;
    
    TeamData(@NotNull EnumTeam team) {
        this.team = team;
    }
    
    public int getKills() {
        return kills;
    }
    
    public int getDeaths() {
        return deaths;
    }
    
    @NotNull
    public EnumTeam getTeam() {
        return team;
    }
    
    @Override
    public int compareTo(@NotNull TeamData that) {
        return Integer.compare(this.kills, that.kills);
    }
    
    public boolean hasKills() {
        return kills > 0;
    }
    
    public boolean hasDeaths() {
        return deaths > 0;
    }
    
}