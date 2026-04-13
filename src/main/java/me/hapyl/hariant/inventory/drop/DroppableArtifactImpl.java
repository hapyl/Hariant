package me.hapyl.hariant.inventory.drop;

import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.inventory.item.artifact.ItemArtifact;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.UUID;

public class DroppableArtifactImpl implements Droppable {
    
    private final ItemArtifact artifact;
    private final Component name;
    
    private final int weight;
    
    DroppableArtifactImpl(@NotNull ItemArtifact artifact, final int weight) {
        this.artifact = artifact;
        this.name = Component.empty().append(artifact.getName()).append(Component.text(" (Artifact)", NamedTextColor.DARK_GRAY));
        this.weight = weight;
    }
    
    @Range(from = 0, to = Integer.MAX_VALUE)
    @Override
    public int getWeight() {
        return weight;
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @NotNull
    @Override
    public Amount getAmount() {
        return Amount.fixed(1);
    }
    
    @NotNull
    @Override
    public Drop drop(@NotNull PlayerProfile profile) {
        final PlayerDatabase database = profile.getDatabase();
        database.inventory.createItem(artifact.newInstance(database, UUID.randomUUID()));
        
        return new Drop(this, 1);
    }
    
}
