package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.inventory.item.Item;
import me.hapyl.hariant.inventory.item.Rarity;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSet;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ItemArtifact extends Item {
    
    private final ArtifactSet artifactSet;
    
    public ItemArtifact(@NotNull Key key, @NotNull Icon icon, @NotNull ArtifactSet artifactSet, @NotNull Component name, @NotNull Component description) {
        super(key, name, icon);
        
        this.description = description;
        this.artifactSet = artifactSet;
        
        // Default to FIVE_STAR
        this.rarity = Rarity.FIVE_STAR;
    }
    
    public @NotNull ArtifactSet getArtifactSet() {
        return artifactSet;
    }
    
    @NotNull
    @Override
    public ItemBuilder createBuilder() {
        return this.createBuilder(ArtifactDescription.EMPTY);
    }
    
    public final @NotNull ItemBuilder createBuilder(@NotNull ItemArtifact.ArtifactDescription description) {
        final ItemBuilder builder = icon.createBuilder();
        builder.setName(name);
        
        // Append description
        builder.addLore();
        builder.addWrappedLore(this.description, HariantConstants.COMPONENT_STYLER_DESCRIPTION);
        
        // Append affix
        final Component affix = description.getAffix();
        
        if (Component.IS_NOT_EMPTY.test(affix)) {
            builder.addLore();
            builder.addLore(Component.text("Affixes", Colors.GREEN));
            builder.addLore(Component.space().append(affix));
        }
        
        builder.addLore();
        
        // Append artifact set description
        artifactSet.appendDescription(builder, description);
        
        // Append flavor text
        if (Component.IS_NOT_EMPTY.test(flavorText)) {
            builder.addLore();
            builder.addWrappedLore(flavorText, HariantConstants.COMPONENT_STYLER_FLAVOR_TEXT);
        }
        
        return builder;
    }
    
    @NotNull
    @Override
    public ItemArtifactInstance newInstance(@NotNull PlayerDatabase database, @NotNull UUID uuid) {
        return new ItemArtifactInstance(database, this, uuid);
    }
    
    public interface ArtifactDescription extends ArtifactSet.ArtifactSetDescription {
        
        ArtifactDescription EMPTY = new ArtifactDescription() {
            @Override
            public @NotNull Component getArtifactSetNameSuffix(@NotNull ArtifactSet artifactSet) {
                return Component.empty();
            }
            
            @Override
            public @NotNull Component getPieceNameSuffix(@NotNull ArtifactSet artifactSet, @NotNull PieceCount pieceCount) {
                return Component.empty();
            }
            
            @Override
            public @NotNull Component getAffix() {
                return Component.empty();
            }
        };
        
        @NotNull Component getAffix();
        
    }
    
}