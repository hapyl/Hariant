package me.hapyl.hariant.team;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public final class TeamEntryImpl implements TeamEntry {
    
    private final UUID uuid;
    private final boolean isPlayer;
    
    TeamEntryImpl(@NotNull UUID uuid, boolean isPlayer) {
        this.uuid = uuid;
        this.isPlayer = isPlayer;
    }
    
    @NotNull
    @Override
    public UUID getUuid() {
        return uuid;
    }
    
    @Override
    public boolean isPlayer() {
        return isPlayer;
    }
    
    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final TeamEntryImpl that = (TeamEntryImpl) object;
        return Objects.equals(this.uuid, that.uuid);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.uuid);
    }
    
}
