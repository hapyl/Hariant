package me.hapyl.hariant.database.rank;

import me.hapyl.hariant.util.ComparableOrdinal;
import org.jetbrains.annotations.NotNull;

public interface Rank extends ComparableOrdinal<Rank> {
    
    int permissionLevel();
    
    @Override
    default int ordinal() {
        return permissionLevel();
    }
    
    @NotNull
    RankFormatter formatter();
    
    default boolean isStaff() {
        return permissionLevel() >= PlayerRank.PERMISSION_LEVEL_STAFF;
    }
    
}
