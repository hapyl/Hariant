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
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class HeroInstance implements Instance<Hero>, MongoSerializable, Hoverable, ItemCreator, ArtifactHolder, Holder {
    
    private static final Style HOLDER_STYLE = Style.style(Colors.GRAY, TextDecoration.UNDERLINED);
    
    private final PlayerDatabase playerDatabase;
    private final Hero hero;
    private final ArtifactMap artifactMap;
    private final ArtifactFilter artifactFilter;
    private final ArtifactLoadouts artifactLoadouts;
    private final Component holderName;
    
    private @NotNull Timestamp timestamp;
    
    public HeroInstance(@NotNull PlayerDatabase playerDatabase, @NotNull Hero hero) {
        this.playerDatabase = playerDatabase;
        this.hero = hero;
        this.artifactMap = new ArtifactMap(this);
        this.artifactFilter = new ArtifactFilter();
        this.artifactLoadouts = new ArtifactLoadouts();
        this.timestamp = Timestamp.ofNow();
        this.holderName = Component.empty()
                                   .append(Component.text("Equipped by ", HOLDER_STYLE))
                                   .append(hero.asHeadComponent().style(HOLDER_STYLE))
                                   .append(Component.text(" ", HOLDER_STYLE))
                                   .append(hero.getName().style(HOLDER_STYLE));
    }
    
    public @NotNull ArtifactLoadouts getArtifactLoadouts() {
        return artifactLoadouts;
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
        
        // Write artifact loadouts
        if (!artifactLoadouts.isEmpty()) {
            document.put("artifact_loadouts", artifactLoadouts.writeToNewDocument(playerDatabase, problemReporter));
        }
    }
    
    @Override
    public void read(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        // Read timestamp
        timestamp = MongoCodecs.ofTimestamp().read(document, "timestamp").orElseGet(Timestamp::ofNow);
        
        // Read artifacts
        artifactMap.read(playerDatabase, document.get("artifacts", new Document()), problemReporter);
        
        // Read loadouts
        artifactLoadouts.read(playerDatabase, document.get("artifact_loadouts", new Document()), problemReporter);
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
        return hero.createBuilder();
    }
    
    @Override
    public @NotNull Component getHolderName() {
        return holderName;
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
    public @NotNull Stream<ItemArtifactInstance> streamArtifacts() {
        return artifactMap.streamArtifacts();
    }
    
    public @NotNull ArtifactFilter getArtifactFilter() {
        // FIXME (xanyjl @ Saturday, July 4) -> Remove this from impl just keep it here and call hero#getArtifactFilter()
        return artifactFilter;
    }
    
    @Override
    public @NotNull PieceCount countArtifactSetPieces(@NotNull ArtifactSet artifactSet) {
        return artifactMap.countArtifactSetPieces(artifactSet);
    }
    
    @Override
    public boolean isArtifactSetPieceBonusActive(@NotNull ArtifactSet artifactSet, @NotNull PieceCount pieceCount) {
        return artifactMap.isArtifactSetPieceBonusActive(artifactSet, pieceCount);
    }
    
    @Override
    public @NotNull Map<ArtifactSet, PieceCount> countArtifactSetPieces() {
        return artifactMap.countArtifactSetPieces();
    }
    
    @Override
    public @NotNull Map<? extends @NotNull AttributeType, ? extends @NotNull Double> sumArtifactAffixes() {
        return artifactMap.sumArtifactAffixes();
    }
    
}