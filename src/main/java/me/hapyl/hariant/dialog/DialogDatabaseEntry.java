package me.hapyl.hariant.dialog;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.math.Numbers;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.annotate.MapGuide;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.PlayerDatabaseEntry;
import me.hapyl.hariant.database.problem.ProblemReporter;
import me.hapyl.hariant.database.serialize.MongoSerializableConstructor;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class DialogDatabaseEntry extends PlayerDatabaseEntry {
    
    @MapGuide(
            key = "The dialog key.",
            value = "The completion timestamp."
    )
    public final Map<Key, Long> dialogsCompleted;
    
    @MongoSerializableConstructor
    private DialogDatabaseEntry(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull String parent) {
        super(database, document, parent);
        
        this.dialogsCompleted = Maps.newHashMap();
    }
    
    @Override
    public void write(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        dialogsCompleted.forEach((key, timestamp) -> document.put(key.getKey(), timestamp));
    }
    
    @Override
    public void read(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        document.forEach((stringKey, value) -> {
            final Key key = Key.ofStringOrNull(stringKey);
            final long timestamp = Numbers.toLong(value);
            
            dialogsCompleted.put(key, timestamp);
        });
    }
    
}
