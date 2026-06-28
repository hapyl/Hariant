package me.hapyl.hariant.hero;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.database.Instance;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.problem.ProblemReporter;
import me.hapyl.hariant.database.serialize.MongoSerializable;
import me.hapyl.hariant.database.serialize.codec.MongoCodecs;
import me.hapyl.hariant.inventory.item.ItemCreator;
import me.hapyl.hariant.inventory.item.artifact.ArtifactFilter;
import me.hapyl.hariant.inventory.item.artifact.ArtifactSlot;
import me.hapyl.hariant.inventory.item.artifact.ItemArtifactInstance;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSet;
import me.hapyl.hariant.util.Hoverable;
import me.hapyl.hariant.util.Timestamp;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class HeroInstance implements Instance<Hero>, MongoSerializable, Hoverable, ItemCreator, ArtifactHolder {
    
    private final PlayerDatabase playerDatabase;
    private final Hero hero;
    private final ArtifactMap artifactMap;
    
    private @NotNull Timestamp timestamp;
    private @NotNull ArtifactFilter artifactFilter;
    
    public HeroInstance(@NotNull PlayerDatabase playerDatabase, @NotNull Hero hero) {
        this.playerDatabase = playerDatabase;
        this.hero = hero;
        this.artifactMap = new ArtifactMap(this);
        this.timestamp = Timestamp.ofNow();
        this.artifactFilter = new ArtifactFilter();
    }
    
    public @NotNull ArtifactMap getArtifactMap() {
        return artifactMap;
    }
    
    @NotNull
    @Override
    public PlayerDatabase getDatabase() {
        return playerDatabase;
    }
    
    @NotNull
    @Override
    public Hero getOrigin() {
        return hero;
    }
    
    @Override
    public void onInstanceCreated() {
    }
    
    @Override
    public void onInstanceDestroyed() {
    }
    
    @Override
    public void write(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        // Write timestamp
        MongoCodecs.ofTimestamp().write(document, "timestamp", timestamp);
        
        // Write artifacts
        document.put("artifacts", artifactMap.writeToNewDocument(playerDatabase, problemReporter));
        
        // Write artifact filter
        if (!artifactFilter.isEmpty()) {
            document.put("artifact_filter", artifactFilter.writeToNewDocument(playerDatabase, problemReporter));
        }
    }
    
    @Override
    public void read(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        // Read timestamp
        timestamp = MongoCodecs.ofTimestamp().read(document, "timestamp").orElseGet(Timestamp::ofNow);
        
        // Read artifacts
        artifactMap.read(playerDatabase, document.get("artifacts", new Document()), problemReporter);
        
        // Read artifact filter
        artifactFilter.read(playerDatabase, document.get("artifact_filter", new Document()), problemReporter);
    }
    
    @Override
    @NotNull
    public HoverEvent<?> createHoverEvent() {
        return HoverEvent.showText(
                Component.empty()
                         .append(Component.text("Instance: ", Colors.DARK_AQUA))
                         .append(Component.text(this.getClass().getSimpleName(), Colors.AQUA))
                         .appendNewline()
                         .append(Component.text("Timestamp: ", Colors.DARK_AQUA))
                         .append(timestamp.asComponent().color(Colors.AQUA))
        );
    }
    
    @NotNull
    @Override
    public ItemBuilder createBuilder() {
        final ItemBuilder builder = hero.createBuilder();
        
        // Append stats
        // FIXME (xanyjl @ Tuesday, June 23) -> ?
        
        return builder;
    }
    
    @Override
    public @NotNull Optional<ItemArtifactInstance> getArtifact(@NotNull ArtifactSlot artifactSlot) {
        return artifactMap.getArtifact(artifactSlot);
    }
    
    @Override
    public void setArtifact(@NotNull ItemArtifactInstance artifact) {
        artifactMap.setArtifact(artifact);
    }
    
    @Override
    public void unsetArtifact(@NotNull ItemArtifactInstance artifact) {
        artifactMap.unsetArtifact(artifact);
    }
    
    @Override
    public void unsetArtifacts() {
        artifactMap.unsetArtifacts();
    }
    
    @Override
    public boolean isArtifactSetPieceBonusActive(@NotNull ArtifactSet artifactSet, @NotNull PieceCount pieceCount) {
        return artifactMap.isArtifactSetPieceBonusActive(artifactSet, pieceCount);
    }
    
    @Override
    public @NotNull PieceCount countArtifactSetPieces(@NotNull ArtifactSet artifactSet) {
        return artifactMap.countArtifactSetPieces(artifactSet);
    }
    
    @Override
    public @NotNull Map<ArtifactSet, PieceCount> countArtifactSetPieces() {
        return artifactMap.countArtifactSetPieces();
    }
    
    @Override
    public @NotNull Map<? extends @NotNull AttributeType, ? extends @NotNull Double> sumArtifactAffixes() {
        return artifactMap.sumArtifactAffixes();
    }
    
    @Override
    public @NotNull Stream<ItemArtifactInstance> streamArtifacts() {
        return artifactMap.streamArtifacts();
    }
    
    public @NotNull ArtifactFilter getArtifactFilter() {
        if (this.artifactFilter == null) {
            this.artifactFilter = new ArtifactFilter();
        }
        
        return this.artifactFilter;
    }
    
}