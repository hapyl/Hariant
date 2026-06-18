package me.hapyl.hariant.database.async;

import me.hapyl.hariant.database.Database;
import me.hapyl.hariant.database.DatabaseCollection;
import me.hapyl.hariant.shop.ShopTransaction;
import org.jetbrains.annotations.NotNull;

public class TransactionDatabaseAsyncCollection extends DatabaseAsyncCollection {
    
    public TransactionDatabaseAsyncCollection(@NotNull Database database, @NotNull DatabaseCollection collection) {
        super(database, collection);
    }
 
    public void record(@NotNull ShopTransaction shopTransaction) {
        // TODO (xanyjl @ Thursday, May 28) -> Implement this
    }
    
}