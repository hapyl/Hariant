package me.hapyl.hariant.security;

import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.database.DatabaseCollection;
import me.hapyl.hariant.database.async.DatabaseAsyncCollectionSecurity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SecurityManagerImpl implements SecurityManager {
    
    private final DatabaseAsyncCollectionSecurity databaseEntry;
    
    public SecurityManagerImpl(@NotNull Hariant hariant) {
        Objects.requireNonNull(hariant);
        
        this.databaseEntry = new DatabaseAsyncCollectionSecurity(Hariant.getPlugin().getDatabase(), DatabaseCollection.SECURITY);
    }
    
    @Override
    public void kick(@NotNull Player player, @NotNull KickReason kickReason) {
        databaseEntry.report(kickReason);
        player.kick(kickReason.asComponent());
    }
    
}
