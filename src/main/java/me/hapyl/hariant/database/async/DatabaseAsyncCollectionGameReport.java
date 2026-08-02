package me.hapyl.hariant.database.async;

import me.hapyl.hariant.database.Database;
import me.hapyl.hariant.database.DatabaseCollection;
import me.hapyl.hariant.game.GameInstance;
import me.hapyl.hariant.util.HexId;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DatabaseAsyncCollectionGameReport extends DatabaseAsyncCollection {
    
    public DatabaseAsyncCollectionGameReport(@NotNull Database database, @NotNull DatabaseCollection collection) {
        super(database, collection);
    }
    
    public @NotNull List<? extends GameReport> gameReport(@NotNull HexId hexId) {
        return List.of();
    }
    
    public void gameReport(@NotNull GameReport gameReport) {
    }
    
    public record GameReport() {
        
        public static @NotNull GameReport fromGameInstance(@NotNull GameInstance gameInstance) {
            return new GameReport();
        }
        
    }
    
}
