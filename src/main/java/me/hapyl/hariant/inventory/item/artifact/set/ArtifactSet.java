package me.hapyl.hariant.inventory.item.artifact.set;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.annotate.AutoRegisteredListener;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.registry.Registrable;
import me.hapyl.hariant.util.LoreSupplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

@AutoRegisteredListener
public abstract class ArtifactSet implements Keyed, Named, Registrable, ComponentLike, LoreSupplier {
    
    private final Key key;
    private final Component name;
    
    private final Map<PieceCount, Component> effectDescription;
    
    private @Nullable Set<? extends AttributeType> artifactTags;
    
    ArtifactSet(@NotNull Key key, @NotNull Component name) {
        this.key = key;
        this.name = name;
        this.effectDescription = Maps.newEnumMap(PieceCount.class);
        
        AutoRegisteredListener.Registry.register(this);
    }
    
    @Override
    public @NotNull Component asComponent() {
        return name;
    }
    
    @Override
    public void onRegister() {
    }
    
    @Override
    public void onUnregister() {
    }
    
    public @Nullable ElementType getEffectiveElementType() {
        return null;
    }
    
    public @Nullable Set<? extends AttributeType> getArtifactTags() {
        return artifactTags;
    }
    
    public void setArtifactTags(@NotNull AttributeType... artifactTags) {
        this.artifactTags = Set.of(artifactTags);
    }
    
    public int totalEffects() {
        return effectDescription.size();
    }
    
    public int firstEffectPiece() {
        return streamEffectsAsOrdinals().min(Integer::compare).orElse(0);
    }
    
    public int lastEffectPiece() {
        return streamEffectsAsOrdinals().max(Integer::compare).orElse(0);
    }
    
    @ApiStatus.OverrideOnly
    public abstract void applyEffect(@NotNull HariantPlayer player, @NotNull PieceCount pieceCount);
    
    @NotNull
    @Override
    public final Key getKey() {
        return key;
    }
    
    @Override
    public final int hashCode() {
        return Objects.hashCode(this.key);
    }
    
    @Override
    public final boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final ArtifactSet that = (ArtifactSet) object;
        return Objects.equals(this.key, that.key);
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @Nullable
    public Component getPieceDescription(@NotNull PieceCount pieceCount) {
        return effectDescription.get(pieceCount);
    }
    
    @Override
    public void supplyLore(@NotNull ItemBuilder builder) {
        this.supplyLore(builder, ArtifactSetDescription.EMPTY);
    }
    
    public void supplyLore(@NotNull ItemBuilder builder, @NotNull ArtifactSetDescription description) {
        // Append artifact set name
        builder.addLore(
                Component.empty()
                         .append(name.color(Colors.GREEN))
                         .append(description.getArtifactSetNameSuffix(this))
        );
        
        // Add piece description
        int index = 0;
        
        for (PieceCount pieceCount : PieceCount.values()) {
            final Component pieceDescription = this.getPieceDescription(pieceCount);
            
            if (pieceDescription == null) {
                continue;
            }
            
            if (index++ != 0) {
                builder.addLore();
            }
            
            // Append piece count
            builder.addLore(
                    Component.empty()
                             .append(Component.text(" "))
                             .append(pieceCount.getName().color(Colors.DARK_GRAY))
                             .append(Component.text("  "))
                             .append(description.getPieceNameSuffix(this, pieceCount))
            );
            
            // Append piece description
            builder.addWrappedLore(
                    pieceDescription,
                    HariantConstants.COMPONENT_STYLER_DESCRIPTION_PADDING_2
            );
        }
    }
    
    protected void setPieceDescription(@NotNull PieceCount pieceCount, @NotNull Component description) {
        this.effectDescription.put(pieceCount, description);
    }
    
    protected void setPieceDescription(@NotNull PieceCount pieceCount, @NotNull ComponentLike description) {
        this.setPieceDescription(pieceCount, description.asComponent());
    }
    
    private @NotNull Stream<Integer> streamEffectsAsOrdinals() {
        return effectDescription.keySet().stream().map(PieceCount::ordinal);
    }
    
    public interface ArtifactSetDescription {
        
        ArtifactSetDescription EMPTY = new ArtifactSetDescription() {
            @Override
            public @NotNull Component getArtifactSetNameSuffix(@NotNull ArtifactSet artifactSet) {
                return Component.empty();
            }
            
            @Override
            public @NotNull Component getPieceNameSuffix(@NotNull ArtifactSet artifactSet, @NotNull PieceCount pieceCount) {
                return Component.empty();
            }
        };
        
        @NotNull Component getArtifactSetNameSuffix(@NotNull ArtifactSet artifactSet);
        
        @NotNull Component getPieceNameSuffix(@NotNull ArtifactSet artifactSet, @NotNull PieceCount pieceCount);
        
    }
    
}