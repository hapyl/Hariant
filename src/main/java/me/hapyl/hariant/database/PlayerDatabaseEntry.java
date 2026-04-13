package me.hapyl.hariant.database;

import me.hapyl.hariant.database.problem.ProblemReporter;
import me.hapyl.hariant.database.serialize.MongoSerializable;
import me.hapyl.hariant.database.serialize.MongoSerializableConstructor;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an entry in a {@link PlayerDatabase}.
 *
 * <p>
 * For a valid serializable entry, the following requirements must be fulfilled:
 * <ul>
 *     <li>A {@code private} constructor with {@link PlayerDatabase}, {@link Document} and {@link String} as its parameters, as well as
 *     {@link MongoSerializableConstructor} annotation present.
 *
 *     <li>{@link MongoSerializable#write(PlayerDatabase, Document, ProblemReporter)} and {@link MongoSerializable#read(PlayerDatabase, Document, ProblemReporter)} implemented.
 * </ul>
 * If any of the requirements are not fulfilled, the server will shut down with an error message describing the issue.
 * </p>
 */
public abstract class PlayerDatabaseEntry implements MongoSerializable {
    
    protected final PlayerDatabase database;
    protected final Document document;
    
    private final String parent;
    
    public PlayerDatabaseEntry(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull String parent) {
        this.database = database;
        this.parent = parent;
        this.document = document;
    }
    
    @NotNull
    public PlayerDatabase getDatabase() {
        return database;
    }
    
    @NotNull
    public Document getDocument() {
        return document;
    }
    
    @NotNull
    public String getParent() {
        return parent;
    }
    
}
