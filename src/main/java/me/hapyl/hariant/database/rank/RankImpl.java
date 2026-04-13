package me.hapyl.hariant.database.rank;

import org.jetbrains.annotations.NotNull;

public class RankImpl implements Rank {
    
    private final int permissionLevel;
    private final RankFormatter formatter;
    
    RankImpl(int permissionLevel, @NotNull RankFormatter formatter) {
        this.permissionLevel = permissionLevel;
        this.formatter = formatter;
    }
    
    @Override
    public int permissionLevel() {
        return permissionLevel;
    }
    
    @NotNull
    @Override
    public RankFormatter formatter() {
        return formatter;
    }
}
