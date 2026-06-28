package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.problem.ProblemReporter;
import me.hapyl.hariant.database.serialize.codec.MongoCodecs;
import me.hapyl.hariant.hero.HeroInstance;
import me.hapyl.hariant.inventory.item.ItemInstance;
import me.hapyl.hariant.inventory.item.artifact.affix.ArtifactAffix;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSet;
import me.hapyl.hariant.util.Owned;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ItemArtifactInstance extends ItemInstance implements Owned<HeroInstance> {
    
    private static final Style STYLE_EQUIPPED_BY = Style.style(Colors.WHITE, TextDecoration.UNDERLINED);
    
    /**
     * Defines the {@link HeroInstance} this {@link ItemArtifactInstance} is currently equipped by, or {@code null} if not equipped.
     */
    private @Nullable HeroInstance owner;
    
    private @NotNull ArtifactSlot artifactSlot;
    private @NotNull ArtifactAffix artifactAffix;
    
    public ItemArtifactInstance(@NotNull PlayerDatabase playerDatabase, @NotNull ItemArtifact origin, @NotNull UUID uuid) {
        super(playerDatabase, origin, uuid);
        
        this.artifactSlot = ArtifactSlot.SLOT_1;
        this.artifactAffix = ArtifactAffix.MAX_HEATH;
    }
    
    @Override
    public void onInstanceCreated() {
        // Randomize slot and affix the first time artifact is created
        final ArtifactSlot artifactSlot = ArtifactSlot.ofRandom();
        
        this.artifactSlot = artifactSlot;
        this.artifactAffix = artifactSlot.getArtifactAttributeDistribution().randomAffix();
    }
    
    public @NotNull ArtifactSlot getArtifactSlot() {
        return artifactSlot;
    }
    
    public @NotNull ArtifactAffix getArtifactAffix() {
        return artifactAffix;
    }
    
    public void setArtifactAffix(@NotNull ArtifactAffix artifactAffix) {
        this.artifactAffix = artifactAffix;
    }
    
    @NotNull
    public ArtifactSet getArtifactSet() {
        return getOrigin().getArtifactSet();
    }
    
    @Nullable
    @Override
    public HeroInstance getOwner() {
        return owner;
    }
    
    @Override
    public void setOwner(@Nullable HeroInstance owner) {
        this.owner = owner;
    }
    
    @NotNull
    @Override
    public ItemArtifact getOrigin() {
        return (ItemArtifact) super.getOrigin();
    }
    
    @Override
    public void write(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        super.write(database, document, problemReporter);
        
        // Write slot
        MongoCodecs.ofEnum(ArtifactSlot.class).write(document, "artifact_slot", artifactSlot);
        
        // Write affix
        MongoCodecs.ofEnum(ArtifactAffix.class).write(document, "artifact_affix", artifactAffix);
    }
    
    @Override
    public void read(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        super.read(database, document, problemReporter);
        
        // Read slot
        artifactSlot = MongoCodecs.ofEnum(ArtifactSlot.class).read(document, "artifact_slot").orElse(ArtifactSlot.SLOT_1);
        
        // Read affix
        artifactAffix = MongoCodecs.ofEnum(ArtifactAffix.class).read(document, "artifact_affix").orElse(ArtifactAffix.MAX_HEATH);
    }
    
    @Override
    @NotNull
    public ItemBuilder createBuilder() {
        final ItemBuilder builder = getOrigin().createBuilder(new ArtifactInstanceArtifactDescription());
        builder.setName(origin.getName().appendSpace().append(artifactSlot));
        
        // Append owner if exists
        if (owner != null) {
            builder.addLore();
            builder.addLore(
                    Component.empty()
                             .append(Component.text("Equipped by ", STYLE_EQUIPPED_BY))
                             .append(owner.getOrigin().getName().style(STYLE_EQUIPPED_BY))
            );
        }
        
        return builder;
    }
    
    public class ArtifactInstanceArtifactDescription implements ItemArtifact.ArtifactDescription {
        
        ArtifactInstanceArtifactDescription() {
        }
        
        @Override
        public @NotNull Component getArtifactSetNameSuffix(@NotNull ArtifactSet artifactSet) {
            final int artifactSetCount = owner != null ? owner.countArtifactSetPieces(artifactSet).ordinal() : 0;
            
            return artifactSetCount == 0
                   ? Component.empty()
                   : Component.text(" (%s/%s)".formatted(artifactSetCount, artifactSet.lastEffectPiece()), Colors.GRAY);
        }
        
        @Override
        public @NotNull Component getPieceNameSuffix(@NotNull ArtifactSet artifactSet, @NotNull PieceCount pieceCount) {
            if (owner == null) {
                return Component.empty();
            }
            
            final boolean isPieceBonusActive = owner.isArtifactSetPieceBonusActive(artifactSet, pieceCount);
            
            return Components.checkmark(isPieceBonusActive)
                             .appendSpace()
                             .append(isPieceBonusActive ? Component.text("ᴀᴄᴛɪᴠᴇ", Colors.GREEN) : Component.text("ɪɴᴀᴄᴛɪᴠᴇ", Colors.RED));
        }
        
        @Override
        public @NotNull Component getAffix() {
            return artifactAffix.asComponent();
        }
    }
    
}