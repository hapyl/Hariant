package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.inventory.item.Item;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ItemArtifact extends Item {
    
    private final ArtifactSet artifactSet;
    
    public ItemArtifact(@NotNull Key key, @NotNull Component name, @NotNull Icon icon, @NotNull ArtifactSet artifactSet) {
        super(key, name, icon);
        
        this.artifactSet = artifactSet;
    }
    
    @NotNull
    public ArtifactSet getArtifactSet() {
        return artifactSet;
    }
    
    @NotNull
    @Override
    public ItemArtifactInstance newInstance(@NotNull PlayerDatabase database, @NotNull UUID uuid) {
        return new ItemArtifactInstance(database, this, uuid);
    }
}
