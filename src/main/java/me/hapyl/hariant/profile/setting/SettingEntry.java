package me.hapyl.hariant.profile.setting;

import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.PlayerDatabaseEntry;
import me.hapyl.hariant.database.problem.ProblemReporter;
import me.hapyl.hariant.database.serialize.MongoSerializableConstructor;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

public class SettingEntry extends PlayerDatabaseEntry {
    
    @MongoSerializableConstructor
    private SettingEntry(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull String parent) {
        super(database, document, parent);
    }
    
    public <I> I getValue(@NotNull Setting<I> setting) {
        return setting.getValue(document);
    }
    
    public <I> void setValue(@NotNull Setting<I> setting, @NotNull I value) {
       setting.setValue(document, value);
    }
    
    @Override
    public void write(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
    }
    
    @Override
    public void read(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
    }
    
}
