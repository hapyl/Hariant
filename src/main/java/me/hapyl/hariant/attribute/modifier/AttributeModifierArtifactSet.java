package me.hapyl.hariant.attribute.modifier;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.text.Capitalizable;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSet;
import me.hapyl.hariant.inventory.item.artifact.set.modifier.ArtifactSetModifier;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class AttributeModifierArtifactSet extends AttributeModifier {
    
    public AttributeModifierArtifactSet(@NotNull ModifierKey modifierKey, @NotNull HariantEntity applier, int duration) {
        super(modifierKey.key, modifierKey.name, applier, duration);
    }
    
    public AttributeModifierArtifactSet(@NotNull ArtifactSet artifactSet, @NotNull PieceCount pieceCount, @NotNull HariantEntity applier, int duration) {
        this(ModifierKey.create(artifactSet, pieceCount), applier, duration);
    }
    
    public AttributeModifierArtifactSet(@NotNull ArtifactSet artifactSet, @NotNull PieceCount pieceCount, @NotNull HariantEntity applier, @NotNull ArtifactSetModifier modifier) {
        this(artifactSet, pieceCount, applier, HariantConstants.INDEFINITE_DURATION);
        
        this.of(modifier.getAttributeType(), modifier.getModifierType(), modifier.getValue());
    }
    
    public static final class ModifierKey {
        
        private final Key key;
        private final Component name;
        
        ModifierKey(@NotNull Key key, @NotNull Component name) {
            this.key = key;
            this.name = name;
        }
        
        public @NotNull Key key() {
            return key;
        }
        
        public @NotNull Component name() {
            return name;
        }
        
        public static @NotNull ModifierKey create(@NotNull ArtifactSet artifactSet, @NotNull PieceCount pieceCount) {
            return new ModifierKey(createModifierKey(artifactSet, pieceCount), createModifierName(artifactSet, pieceCount));
        }
        
        private static @NotNull Key createModifierKey(@NotNull ArtifactSet artifactSet, @NotNull PieceCount pieceCount) {
            return Key.ofString("%s_%s".formatted(artifactSet.getKey(), pieceCount.name().toLowerCase()));
        }
        
        private static @NotNull Component createModifierName(@NotNull ArtifactSet artifactSet, @NotNull PieceCount pieceCount) {
            return artifactSet.getName().append(Component.text(" (%s)".formatted(Capitalizable.capitalize(pieceCount))));
        }
        
    }
    
}