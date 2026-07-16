package me.hapyl.hariant.shop.transaction;

import me.hapyl.hariant.database.PlayerDatabase;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class TransactionImpl implements Transaction {
    
    private final UUID uuid;
    
    TransactionImpl() {
        this.uuid = UUID.randomUUID();
    }
    
    @Override
    public @NotNull UUID getUuid() {
        return uuid;
    }
    
    @Override
    public abstract @NotNull TransactionResult process(@NotNull Player player, @NotNull PlayerDatabase playerDatabase) throws TransactionException;
    
}