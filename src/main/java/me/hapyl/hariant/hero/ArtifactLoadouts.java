package me.hapyl.hariant.hero;

import com.google.common.collect.Maps;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.problem.ProblemReporter;
import me.hapyl.hariant.database.serialize.MongoSerializable;
import me.hapyl.hariant.inventory.item.artifact.ItemArtifactInstance;
import me.hapyl.hariant.inventory.item.artifact.loadout.ArtifactLoadout;
import me.hapyl.hariant.inventory.item.artifact.loadout.ArtifactLoadoutIndex;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public class ArtifactLoadouts implements MongoSerializable {
    
    private final Map<ArtifactLoadoutIndex, ArtifactLoadout> loadouts;
    
    public ArtifactLoadouts() {
        this.loadouts = Maps.newEnumMap(ArtifactLoadoutIndex.class);
    }
    
    public @NotNull Optional<ArtifactLoadout> getLoadout(@NotNull ArtifactLoadoutIndex index) {
        return Optional.ofNullable(loadouts.get(index));
    }
    
    public void setLoadout(@NotNull ArtifactLoadoutIndex index, @NotNull ItemArtifactInstance[] artifacts) {
        loadouts.put(index, new ArtifactLoadout(index, artifacts));
    }
    
    @Override
    public void write(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        loadouts.forEach((index, loadout) -> document.put(index.name().toLowerCase(), loadout.writeToNewDocument(database, problemReporter)));
    }
    
    @Override
    public void read(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        for (ArtifactLoadoutIndex index : ArtifactLoadoutIndex.values()) {
            if (document.get(index.name().toLowerCase()) instanceof Document loadoutDocument) {
                final ArtifactLoadout loadout = ArtifactLoadout.read0(index, database, loadoutDocument, problemReporter);
             
                // if `read0` returned null, there was an issue loading a loadout, but it's fine since loadouts aren't critical
                if (loadout != null) {
                    loadouts.put(index, loadout);
                }
            }
        }
    }
    
    public boolean doesIdenticalExist(@NotNull ItemArtifactInstance[] array) {
        for (ArtifactLoadout loadout : loadouts.values()) {
            if (loadout.isIdentical(array)) {
                return true;
            }
        }
        
        return false;
    }
    
    public void deleteLoadout(@NotNull ArtifactLoadout loadout) {
        loadouts.remove(loadout.getIndex());
    }
    
    public boolean isEmpty() {
        return loadouts.isEmpty();
    }
    
}
