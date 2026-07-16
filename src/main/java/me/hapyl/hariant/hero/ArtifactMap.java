package me.hapyl.hariant.hero;

import com.google.common.collect.Maps;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.problem.Problem;
import me.hapyl.hariant.database.problem.ProblemReporter;
import me.hapyl.hariant.database.serialize.MongoSerializable;
import me.hapyl.hariant.database.serialize.codec.MongoCodecs;
import me.hapyl.hariant.inventory.item.artifact.ArtifactFilter;
import me.hapyl.hariant.inventory.item.artifact.ArtifactSlot;
import me.hapyl.hariant.inventory.item.artifact.ItemArtifactInstance;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public final class ArtifactMap implements ArtifactHolder, MongoSerializable {
    
    private final HeroInstance heroInstance;
    private final Map<ArtifactSlot, ItemArtifactInstance> artifacts;
    private final ArtifactFilter filter;
    
    public ArtifactMap(@NotNull HeroInstance heroInstance) {
        this.heroInstance = heroInstance;
        this.artifacts = Maps.newEnumMap(ArtifactSlot.class);
        this.filter = new ArtifactFilter();
    }
    
    @Override
    public @NotNull Optional<ItemArtifactInstance> getArtifact(@NotNull ArtifactSlot artifactSlot) {
        return Optional.ofNullable(artifacts.get(artifactSlot));
    }
    
    @Override
    public void setArtifact(@NotNull ItemArtifactInstance artifact) {
        final ItemArtifactInstance previousArtifact = artifacts.put(artifact.getArtifactSlot(), artifact);
        
        // If there was an artifact on the slot, unset the owner
        if (previousArtifact != null) {
            previousArtifact.setHolder(null);
        }
        
        // If the new artifact was held somewhere, unset it
        final ArtifactHolder holder = artifact.getHolder();
        
        if (holder != null) {
            holder.unsetArtifact(artifact);
        }
        
        artifact.setHolder(heroInstance);
    }
    
    @Override
    public void unsetArtifact(@NotNull ItemArtifactInstance artifact) {
        artifacts.remove(artifact.getArtifactSlot());
        artifact.setHolder(null);
    }
    
    @Override
    public void unsetArtifacts() {
        artifacts.values().forEach(artifact -> artifact.setHolder(null));
        artifacts.clear();
    }
    
    @Override
    public @NotNull Stream<ItemArtifactInstance> streamArtifacts() {
        return artifacts.values().stream();
    }
    
    @Override
    public @NotNull ArtifactFilter getArtifactFilter() {
        return heroInstance.getArtifactFilter();
    }
    
    @Override
    public void write(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        // Write slots
        artifacts.forEach((artifactSlot, artifact) -> MongoCodecs.ofUuid().write(document, artifactSlot.name().toLowerCase(), artifact.getUuid()));
        
        // Write filter
        if (!filter.isEmpty()) {
            document.put("filter", filter.writeToNewDocument(database, problemReporter));
        }
    }
    
    @Override
    public void read(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        // Read slots
        for (ArtifactSlot artifactSlot : ArtifactSlot.values()) {
            final UUID uuid = MongoCodecs.ofUuid().read(document, artifactSlot.name().toLowerCase()).orElse(null);
            
            // If uuid is null, it means there are no artifact equipped on this slot
            if (uuid == null) {
                return;
            }
            
            final ItemArtifactInstance artifactInstance = database.inventory.getItemByUuid(uuid, ItemArtifactInstance.class).orElse(null);
            
            if (artifactInstance == null) {
                problemReporter.report(Problem.severe(ArtifactMap.class, "Missing artifact: %s".formatted(uuid)));
                return;
            }
            
            artifactInstance.setHolder(heroInstance);
            
            artifacts.put(artifactSlot, artifactInstance);
        }
        
        // Read filter
        filter.read(database, document.get("filter", new Document()), problemReporter);
    }
    
}