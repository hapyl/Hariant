package me.hapyl.hariant.shop.transaction;

import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.inventory.item.Resource;
import me.hapyl.hariant.util.UniquelyIdentified;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public interface Transaction extends UniquelyIdentified {
    
    @Override
    @NotNull UUID getUuid();
    
    @NotNull TransactionResult process(@NotNull Player player, @NotNull PlayerDatabase playerDatabase) throws TransactionException;
    
    @NotNull
    static Transaction withResources(@NotNull Map<Resource, Integer> resourceMap) {
        return new TransactionResourceImpl(resourceMap);
    }
    
}
