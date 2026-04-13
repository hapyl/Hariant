package me.hapyl.hariant.database.async;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import me.hapyl.hariant.database.Database;
import me.hapyl.hariant.database.DatabaseCollection;
import me.hapyl.hariant.task.InternalTasks;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class DatabaseAsyncCollection {
    
    private final Database database;
    private final MongoCollection<Document> collection;
    
    public DatabaseAsyncCollection(@NotNull Database database, @NotNull DatabaseCollection collection) {
        if (!collection.isAsync()) {
            throw new IllegalArgumentException("Expected async collection but got sync one! (%s)".formatted(collection.name()));
        }
        
        this.database = database;
        this.collection = database.getCollection(collection);
    }
    
    @NotNull
    public Database getDatabase() {
        return database;
    }
    
    @NotNull
    public CompletableFuture<Optional<Document>> find(@NotNull Bson filter) {
        final CompletableFuture<Optional<Document>> future = new CompletableFuture<>();
        InternalTasks.asynchronously(() -> future.complete(Optional.ofNullable(collection.find(filter).first())));
        
        return future;
    }
    
    @NotNull
    public CompletableFuture<UpdateResult> update(@NotNull Bson filter, @NotNull Bson update) {
        final CompletableFuture<UpdateResult> future = new CompletableFuture<>();
        InternalTasks.asynchronously(() -> future.complete(collection.updateOne(filter, update)));
        
        return future;
    }
    
    public void insert(@NotNull Document document) {
        InternalTasks.asynchronously(() -> collection.insertOne(document));
    }
    
    @NotNull
    public CompletableFuture<DeleteResult> delete(@NotNull Bson filter) {
        final CompletableFuture<DeleteResult> future = new CompletableFuture<>();
        InternalTasks.asynchronously(() -> future.complete(collection.deleteOne(filter)));
        
        return future;
    }
    
}
