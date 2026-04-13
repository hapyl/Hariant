package me.hapyl.hariant.team;

import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.util.UniquelyIdentified;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface TeamEntry extends UniquelyIdentified {
    
    @NotNull
    @Override
    UUID getUuid();
    
    boolean isPlayer();
    
    @Override
    int hashCode();
    
    @Override
    boolean equals(Object object);
    
    @NotNull
    static TeamEntry create(@NotNull HariantEntity entity) {
        return new TeamEntryImpl(entity.getUuid(), entity instanceof HariantPlayer);
    }
    
    @NotNull
    static TeamEntry create(@NotNull UUID uuid, boolean isPlayer) {
        return new TeamEntryImpl(uuid, isPlayer);
    }
    
    @NotNull
    static TeamEntry create(@NotNull Player player) {
        return new TeamEntryImpl(player.getUniqueId(), true);
    }
    
}