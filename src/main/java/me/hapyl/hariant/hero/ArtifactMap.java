package me.hapyl.hariant.hero;

import com.google.common.collect.Maps;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.problem.Problem;
import me.hapyl.hariant.database.problem.ProblemReporter;
import me.hapyl.hariant.database.serialize.MongoSerializable;
import me.hapyl.hariant.database.serialize.codec.MongoCodecs;
import me.hapyl.hariant.inventory.item.artifact.ArtifactSlot;
import me.hapyl.hariant.inventory.item.artifact.ItemArtifactInstance;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.inventory.item.artifact.affix.ArtifactAffix;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSet;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ArtifactMap implements MongoSerializable, ArtifactHolder {
    
    private final HeroInstance heroInstance;
    private final Map<ArtifactSlot, ItemArtifactInstance> artifacts;
    
    public ArtifactMap(@NotNull HeroInstance heroInstance) {
        this.heroInstance = heroInstance;
        this.artifacts = Maps.newEnumMap(ArtifactSlot.class);
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
            previousArtifact.setOwner(null);
        }
        
        // If the new artifact was equipped somewhere, unset it
        final HeroInstance owner = artifact.getOwner();
        
        if (owner != null) {
            owner.getArtifactMap().unsetArtifact(artifact);
        }
        
        artifact.setOwner(heroInstance);
    }
    
    @Override
    public void unsetArtifact(@NotNull ItemArtifactInstance artifact) {
        artifacts.remove(artifact.getArtifactSlot());
        artifact.setOwner(null);
    }
    
    @Override
    public void unsetArtifacts() {
        artifacts.values().forEach(artifact -> artifact.setOwner(null));
        artifacts.clear();
    }
    
    @Override
    public boolean isArtifactSetPieceBonusActive(@NotNull ArtifactSet artifactSet, @NotNull PieceCount pieceCount) {
        final PieceCount artifactPieceCount = this.countArtifactSetPieces().get(artifactSet);
        
        return artifactPieceCount != null && artifactPieceCount.isOrHigher(pieceCount);
    }
    
    @Override
    public @NotNull PieceCount countArtifactSetPieces(@NotNull ArtifactSet artifactSet) {
        return PieceCount.valueOf((int) this.artifacts.values().stream().filter(artifact -> artifact.getArtifactSet().equals(artifactSet)).count());
    }
    
    @Override
    public @NotNull Map<ArtifactSet, PieceCount> countArtifactSetPieces() {
        return this.streamArtifacts()
                   .map(ItemArtifactInstance::getArtifactSet)
                   .collect(Collectors.groupingBy(
                           Function.identity(),
                           Collectors.collectingAndThen(
                                   Collectors.counting(),
                                   // There is no way this method throws, since it's impossible for an artifact to not have a set
                                   count -> PieceCount.valueOf(count.intValue())
                           )
                   ));
    }
    
    @Override
    public @NotNull Map<? extends @NotNull AttributeType, ? extends @NotNull Double> sumArtifactAffixes() {
        return this.streamArtifacts()
                   .map(ItemArtifactInstance::getArtifactAffix)
                   .collect(Collectors.groupingBy(
                           ArtifactAffix::getAttributeType,
                           Collectors.summingDouble(ArtifactAffix::getValue)
                   ));
    }
    
    @Override
    public @NotNull Stream<ItemArtifactInstance> streamArtifacts() {
        return artifacts.values().stream();
    }
    
    @Override
    public void write(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        artifacts.forEach((artifactSlot, artifact) -> {
            MongoCodecs.ofUuid().write(document, artifactSlot.name().toLowerCase(), artifact.getUuid());
        });
    }
    
    @Override
    public void read(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
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
            
            artifacts.put(artifactSlot, artifactInstance);
        }
    }
    
}