package me.hapyl.hariant.experience;

import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.PlayerDatabaseEntry;
import me.hapyl.hariant.database.problem.ProblemReporter;
import me.hapyl.hariant.database.serialize.MongoSerializableConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

public class LevelEntry extends PlayerDatabaseEntry implements ComponentLike {
    
    private long experience;
    
    @MongoSerializableConstructor
    private LevelEntry(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull String parent) {
        super(database, document, parent);
        
        this.experience = 0;
    }
    
    public long getExperience() {
        return experience;
    }
    
    public void addExperience(@NotNull ExperienceSource source) {
        this.experience += source.getExperience();
    }
    
    public int getLevel() {
        return Level.forExperience(experience).getLevel();
    }
    
    @Override
    public void write(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        document.put("experience", experience);
    }
    
    @Override
    public void read(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        this.experience = document.get("experience", 0L);
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        return Level.forExperience(experience).asComponent();
    }
}
