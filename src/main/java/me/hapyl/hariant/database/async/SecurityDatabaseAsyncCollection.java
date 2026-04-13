package me.hapyl.hariant.database.async;

import me.hapyl.hariant.database.Database;
import me.hapyl.hariant.database.DatabaseCollection;
import me.hapyl.hariant.security.Punishment;
import org.jetbrains.annotations.NotNull;

public class SecurityDatabaseAsyncCollection extends DatabaseAsyncCollection {
    
    public SecurityDatabaseAsyncCollection(@NotNull Database database, @NotNull DatabaseCollection collection) {
        super(database, collection);
    }
    
    public void report(@NotNull Punishment punishment) {
        super.insert(punishment.asDocument());
    }
    
}
