package me.hapyl.hariant.hero;

import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.inventory.item.artifact.ArtifactFilter;
import me.hapyl.hariant.inventory.item.artifact.ArtifactSlot;
import me.hapyl.hariant.inventory.item.artifact.ItemArtifactInstance;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.inventory.item.artifact.affix.ArtifactAffix;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSet;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ArtifactHolder {
    
    @NotNull Optional<ItemArtifactInstance> getArtifact(@NotNull ArtifactSlot artifactSlot);
    
    void setArtifact(@NotNull ItemArtifactInstance artifact);
    
    void unsetArtifact(@NotNull ItemArtifactInstance artifact);
    
    void unsetArtifacts();
    
    @NotNull Stream<ItemArtifactInstance> streamArtifacts();
    
    @NotNull ArtifactFilter getArtifactFilter();
    
    default @NotNull ItemArtifactInstance[] artifactsAsArray() {
        return streamArtifacts().toArray(ItemArtifactInstance[]::new);
    }
    
    default @NotNull PieceCount countArtifactSetPieces(@NotNull ArtifactSet artifactSet) {
        return PieceCount.valueOf((int) this.streamArtifacts().filter(artifact -> artifact.getArtifactSet().equals(artifactSet)).count());
    }
    
    default boolean isArtifactSetPieceBonusActive(@NotNull ArtifactSet artifactSet, @NotNull PieceCount pieceCount) {
        final PieceCount artifactPieceCount = this.countArtifactSetPieces().get(artifactSet);
        
        return artifactPieceCount != null && artifactPieceCount.isOrHigher(pieceCount);
    }
    
    default @NotNull Map<ArtifactSet, PieceCount> countArtifactSetPieces() {
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
    
    default @NotNull Map<? extends @NotNull AttributeType, ? extends @NotNull Double> sumArtifactAffixes() {
        return this.streamArtifacts()
                   .map(ItemArtifactInstance::getArtifactAffix)
                   .collect(Collectors.groupingBy(
                           ArtifactAffix::getAttributeType,
                           Collectors.summingDouble(ArtifactAffix::getValue)
                   ));
    }
    
}