package me.hapyl.hariant.hero;

import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.inventory.item.artifact.ArtifactSlot;
import me.hapyl.hariant.inventory.item.artifact.ItemArtifactInstance;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSet;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface ArtifactHolder {
    
    @NotNull Optional<ItemArtifactInstance> getArtifact(@NotNull ArtifactSlot artifactSlot);
    
    void setArtifact(@NotNull ItemArtifactInstance artifact);
    
    void unsetArtifact(@NotNull ItemArtifactInstance artifact);
    
    void unsetArtifacts();
    
    boolean isArtifactSetPieceBonusActive(@NotNull ArtifactSet artifactSet, @NotNull PieceCount pieceCount);
    
    @NotNull PieceCount countArtifactSetPieces(@NotNull ArtifactSet artifactSet);
    
    @NotNull Map<ArtifactSet, PieceCount> countArtifactSetPieces();
    
    @NotNull Map<? extends @NotNull AttributeType, ? extends @NotNull Double> sumArtifactAffixes();
    
    @NotNull Stream<ItemArtifactInstance> streamArtifacts();
    
}