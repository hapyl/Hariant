package me.hapyl.hariant.database;

import com.google.common.collect.Maps;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.hapyl.hariant.HariantPlugin;
import me.hapyl.hariant.config.HariantConfig;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.util.Map;

public final class Database implements Closeable {
    
    public static final String DATABASE_NAME = "hariant_pvp";
    
    private final HariantPlugin plugin;
    private final MongoClient client;
    private final MongoDatabase database;
    private final Map<DatabaseCollection, MongoCollection<Document>> collections;
    
    public Database(@NotNull HariantPlugin plugin) {
        this.plugin = plugin;
        
        final HariantConfig config = plugin.config();
        
        try {
            this.client = MongoClients.create(config.databaseConnectionLink());
            this.database = client.getDatabase(DATABASE_NAME);
            
            // Load collections
            this.collections = Maps.newEnumMap(DatabaseCollection.class);
            
            for (DatabaseCollection databaseCollection : DatabaseCollection.values()) {
                this.collections.put(databaseCollection, database.getCollection(databaseCollection.getCollectionName()));
            }
        }
        catch (Exception ex) {
            throw HariantPlugin.severeExceptionShutdownServer(new IllegalArgumentException("Error connecting to the database: %s".formatted(ex.getMessage())));
        }
    }
    
    @NotNull
    public MongoCollection<Document> getCollection(@NotNull DatabaseCollection collection) {
        return collections.get(collection);
    }
    
    @Override
    public void close() {
        this.client.close();
    }
}
