package me.hapyl.hariant.hero;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.Enums;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.database.Instance;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.problem.Problem;
import me.hapyl.hariant.database.problem.ProblemReporter;
import me.hapyl.hariant.database.serialize.MongoSerializable;
import me.hapyl.hariant.database.serialize.codec.MongoCodecs;
import me.hapyl.hariant.inventory.item.ItemCreator;
import me.hapyl.hariant.inventory.item.artifact.ArtifactSet;
import me.hapyl.hariant.inventory.item.artifact.ArtifactSlot;
import me.hapyl.hariant.inventory.item.artifact.ItemArtifactInstance;
import me.hapyl.hariant.inventory.item.artifact.PieceCount;
import me.hapyl.hariant.util.Hoverable;
import me.hapyl.hariant.util.Timestamp;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HeroInstance implements Instance<Hero>, MongoSerializable, Hoverable, ItemCreator {
    
    private final PlayerDatabase playerDatabase;
    private final Hero hero;
    
    @NotNull private Timestamp timestamp;
    
    private final Map<ArtifactSlot, ItemArtifactInstance> artifacts;
    
    public HeroInstance(@NotNull PlayerDatabase playerDatabase, @NotNull Hero hero) {
        this.playerDatabase = playerDatabase;
        this.hero = hero;
        this.artifacts = Maps.newEnumMap(ArtifactSlot.class);
        this.timestamp = Timestamp.ofNow();
    }
    
    @NotNull
    public Optional<ItemArtifactInstance> getArtifact(@NotNull ArtifactSlot artifactSlot) {
        return Optional.ofNullable(artifacts.get(artifactSlot));
    }
    
    public void setArtifact(@NotNull ArtifactSlot artifactSlot, @NotNull ItemArtifactInstance artifact) {
        final ItemArtifactInstance previousArtifact = artifacts.put(artifactSlot, artifact);
        
        // Unset owner for the previous artifact
        if (previousArtifact != null) {
            previousArtifact.setOwner(null);
        }
        
        // If the new artifact was equipped somewhere, unset it
        final HeroInstance owner = artifact.getOwner();
        
        if (owner != null) {
            owner.unsetArtifact(artifact);
        }
        
        artifact.setOwner(this);
    }
    
    public void unsetArtifact(@NotNull ArtifactSlot artifactSlot) {
        final ItemArtifactInstance previousArtifact = artifacts.remove(artifactSlot);
        
        if (previousArtifact != null) {
            previousArtifact.setOwner(null);
        }
    }
    
    public void unsetArtifact(@NotNull ItemArtifactInstance artifact) {
        artifacts.values().removeIf(equippedArtifact -> equippedArtifact.equals(artifact));
        
        artifact.setOwner(null);
    }
    
    public boolean isArtifactSetPieceBonusActive(@NotNull ArtifactSet artifactSet, @NotNull PieceCount pieceCount) {
        final PieceCount artifactPieceCount = countArtifactSetPieces().get(artifactSet);
        
        return artifactPieceCount != null && artifactPieceCount.isOrHigher(pieceCount);
    }
    
    @NotNull
    public PieceCount countArtifactSetPieces(@NotNull ArtifactSet artifactSet) {
        return PieceCount.valueOf((int) this.artifacts.values().stream().filter(artifact -> artifact.getArtifactSet().equals(artifactSet)).count());
    }
    
    @NotNull
    public Map<ArtifactSet, PieceCount> countArtifactSetPieces() {
        return this.artifacts.values()
                             .stream()
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
    public void write(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        // Write timestamp
        MongoCodecs.TIMESTAMP.write(document, "timestamp", timestamp);
        
        // Write artifacts
        document.put("artifacts", artifacts.entrySet()
                                           .stream()
                                           .collect(Collectors.toMap(entry -> entry.getKey().name().toLowerCase(), entry -> entry.getValue().getUuid().toString())));
    }
    
    @Override
    public void read(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        // Read timestamp
        timestamp = MongoCodecs.TIMESTAMP.read(document, "timestamp").orElseGet(Timestamp::ofNow);
        
        // Read artifacts
        document.get("artifacts", new Document()).forEach((entryKey, entryValue) -> {
            final ArtifactSlot artifactType = Enums.byName(ArtifactSlot.class, entryKey);
            
            if (artifactType == null) {
                problemReporter.report(Problem.severe(HeroInstance.class, "Invalid artifact slot: %s".formatted(entryKey)));
                return;
            }
            
            if (!(entryValue instanceof String uuidString)) {
                problemReporter.report(Problem.severe(HeroInstance.class, "Artifact UUID must be stored as a String!"));
                return;
            }
            
            final UUID uuid = BukkitUtils.getUuidFromString(uuidString);
            
            if (uuid == null) {
                problemReporter.report(Problem.severe(HeroInstance.class, "Invalid UUID: %s".formatted(uuidString)));
                return;
            }
            
            final ItemArtifactInstance artifactInstance = database.inventory.getItemByUuid(uuid, ItemArtifactInstance.class).orElse(null);
            
            if (artifactInstance == null) {
                problemReporter.report(Problem.warning(HeroInstance.class, "Could not artifact `%s` because it doesn't exist!".formatted(uuidString)));
                return;
            }
            
            artifactInstance.setOwner(this);
            this.artifacts.put(artifactType, artifactInstance);
        });
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
        
        return builder;
    }
    
}