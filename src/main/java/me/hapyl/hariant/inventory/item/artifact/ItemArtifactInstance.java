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
import me.hapyl.hariant.util.Holdable;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ItemArtifactInstance extends ItemInstance implements Holdable<HeroInstance> {
    
    private @Nullable HeroInstance holder;
    
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
    
    public void setArtifactSlot(@NotNull ArtifactSlot artifactSlot) {
        this.artifactSlot = artifactSlot;
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
    
    @Override
    public @Nullable HeroInstance getHolder() {
        return holder;
    }
    
    @Override
    public void setHolder(@Nullable HeroInstance holder) {
        this.holder = holder;
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
    public @NotNull Component getName() {
        return origin.getName().appendSpace().append(artifactSlot);
    }
    
    @Override
    @NotNull
    public ItemBuilder createBuilder() {
        final ItemBuilder builder = getOrigin().createBuilder(new ArtifactInstanceArtifactDescription());
        builder.setName(this.getName());
        
        // Append owner if exists
        if (holder != null) {
            builder.addLore();
            builder.addLore(holder.getHolderName());
        }
        
        return builder;
    }
    
    public class ArtifactInstanceArtifactDescription implements ItemArtifact.ArtifactDescription {
        
        ArtifactInstanceArtifactDescription() {
        }
        
        @Override
        public @NotNull Component getArtifactSetNameSuffix(@NotNull ArtifactSet artifactSet) {
            final int artifactSetCount = holder != null ? holder.countArtifactSetPieces(artifactSet).ordinal() : 0;
            
            return artifactSetCount == 0
                   ? Component.empty()
                   : Component.text(" (%s/%s)".formatted(artifactSetCount, artifactSet.lastEffectPiece()), Colors.GRAY);
        }
        
        @Override
        public @NotNull Component getPieceNameSuffix(@NotNull ArtifactSet artifactSet, @NotNull PieceCount pieceCount) {
            if (holder == null) {
                return Component.empty();
            }
            
            final boolean isPieceBonusActive = holder.isArtifactSetPieceBonusActive(artifactSet, pieceCount);
            
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