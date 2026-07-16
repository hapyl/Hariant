package me.hapyl.hariant.inventory.item.artifact.loadout;

import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.problem.ProblemReporter;
import me.hapyl.hariant.database.serialize.MongoSerializable;
import me.hapyl.hariant.hero.ArtifactHolder;
import me.hapyl.hariant.hero.HeroInstance;
import me.hapyl.hariant.inventory.item.ItemInstance;
import me.hapyl.hariant.inventory.item.artifact.ItemArtifactInstance;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ArtifactLoadout implements MongoSerializable, ComponentLike {
    
    private static final int LOADOUT_LENGTH = 4;
    
    private final ArtifactLoadoutIndex index;
    private final ItemArtifactInstance[] artifacts;
    
    private @NotNull String name;
    
    public ArtifactLoadout(@NotNull ArtifactLoadoutIndex index, @NotNull ItemArtifactInstance[] artifacts) {
        this.index = index;
        this.artifacts = artifacts;
        this.name = index.getDefaultName();
    }
    
    public @NotNull ArtifactLoadoutIndex getIndex() {
        return index;
    }
    
    public @NotNull ItemArtifactInstance[] getArtifacts() {
        return Arrays.copyOf(artifacts, artifacts.length);
    }
    
    public @NotNull String getName() {
        return name;
    }
    
    public void setName(@NotNull String name) {
        this.name = name;
    }
    
    @Override
    public void write(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        document.put("artifacts", Arrays.stream(artifacts).map(ItemInstance::getUuid).map(UUID::toString).toList());
        document.put("name", name);
    }
    
    @Override
    public void read(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        throw new IllegalStateException("Must deserialize via ArtifactLoadout::read0()");
    }
    
    public void equip(@NotNull HeroInstance heroInstance) {
        for (ItemArtifactInstance artifact : artifacts) {
            heroInstance.setArtifact(artifact);
        }
    }
    
    public boolean isIdentical(@NotNull ItemArtifactInstance[] artifacts) {
        if (this.artifacts.length != artifacts.length) {
            return false;
        }
        
        for (int i = 0; i < this.artifacts.length; i++) {
            if (!this.artifacts[i].equals(artifacts[i])) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return Component.text(name);
    }
    
    public boolean isAnyArtifactsEquippedByAnotherHero(@NotNull HeroInstance heroInstance) {
        for (ItemArtifactInstance artifact : artifacts) {
            final ArtifactHolder holder = artifact.getHolder();
            
            if (holder != null && !holder.equals(heroInstance)) {
                return true;
            }
        }
        
        return false;
    }
    
    public static @Nullable ArtifactLoadout read0(@NotNull ArtifactLoadoutIndex index, @NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter) {
        final ItemArtifactInstance[] artifacts = new ItemArtifactInstance[LOADOUT_LENGTH];
        
        // Loadout expects four artifacts in an array, so we have to read it before instantiating the loadout
        if (document.getList("artifacts", String.class) instanceof List<String> list) {
            // We require that the loadout has all four artifacts
            if (list.size() != LOADOUT_LENGTH) {
                return null;
            }
            
            for (int i = 0; i < list.size(); i++) {
                final String string = list.get(i);
                final UUID uuid = BukkitUtils.getUuidFromString(string);
                
                if (uuid == null) {
                    return null;
                }
                
                final ItemArtifactInstance artifactInstance = database.inventory.getItemByUuid(uuid, ItemArtifactInstance.class).orElse(null);
                
                if (artifactInstance == null) {
                    return null;
                }
                
                artifacts[i] = artifactInstance;
            }
        }
        
        // Might as well read name here ¯\_(ツ)_/¯
        final ArtifactLoadout artifactLoadout = new ArtifactLoadout(index, artifacts);
        artifactLoadout.name = document.get("name", index.getDefaultName());
        
        return artifactLoadout;
    }
    
}