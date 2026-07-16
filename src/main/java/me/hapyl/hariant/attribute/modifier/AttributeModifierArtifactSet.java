package me.hapyl.hariant.attribute.modifier;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.text.Capitalizable;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSet;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.ArtifactSetModifier;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class AttributeModifierArtifactSet extends AttributeModifier {
    
    public AttributeModifierArtifactSet(@NotNull ArtifactSet artifactSet, @NotNull PieceCount pieceCount, @NotNull HariantEntity applier, int duration) {
        super(createModifierKey(artifactSet, pieceCount), createModifierName(artifactSet, pieceCount), applier, duration);
    }
    
    public AttributeModifierArtifactSet(@NotNull ArtifactSet artifactSet, @NotNull PieceCount pieceCount, @NotNull HariantEntity applier, int duration, @NotNull ArtifactSetModifier modifier) {
        this(artifactSet, pieceCount, applier, duration);
        
        this.of(modifier.getAttributeType(), modifier.getModifierType(), modifier.getValue());
    }
    
    @NotNull
    private static Key createModifierKey(@NotNull ArtifactSet artifactSet, @NotNull PieceCount pieceCount) {
        return Key.ofString("%s_%s".formatted(artifactSet.getKey(), pieceCount.name().toLowerCase()));
    }
    
    @NotNull
    private static Component createModifierName(@NotNull ArtifactSet artifactSet, @NotNull PieceCount pieceCount) {
        return artifactSet.getName().append(Component.text(" (%s)".formatted(Capitalizable.capitalize(pieceCount))));
    }
    
}
