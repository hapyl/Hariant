package me.hapyl.hariant.shop.transaction;

import me.hapyl.eterna.module.annotate.NotEmpty;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.inventory.HariantInventory;
import me.hapyl.hariant.inventory.item.Resource;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class TransactionResourceImpl extends TransactionImpl {
    
    private final Map<Resource, Integer> resourceMap;
    
    TransactionResourceImpl(@NotNull @NotEmpty Map<Resource, Integer> resourceMap) {
        if (resourceMap.isEmpty()) {
            throw new IllegalArgumentException("Resource map cannot be empty!");
        }
        
        this.resourceMap = resourceMap;
    }
    
    @Override
    public @NotNull TransactionResult process(@NotNull Player player, @NotNull PlayerDatabase playerDatabase) throws TransactionException {
        final HariantInventory inventory = playerDatabase.inventory;
        
        // First check whether player has enough resources
        resourceMap.forEach((resource, required) -> {
            final int amount = inventory.getResource(resource);
            
            if (amount < required) {
                throw new TransactionException("Not enough resource `%s`! (%s/%s)".formatted(resource, amount, required));
            }
        });
        
        // Then, iterate again and actually take resources
        resourceMap.forEach(inventory::removeResource);
        
        return TransactionResult.create(this, () -> resourceMap.forEach(inventory::addResource));
    }
    
}