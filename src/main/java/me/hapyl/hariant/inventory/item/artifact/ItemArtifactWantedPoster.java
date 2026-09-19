package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSetRegistry;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ItemArtifactWantedPoster extends ItemArtifact {
    
    public ItemArtifactWantedPoster(@NotNull Key key) {
        super(
                key,
                Icon.ofTexture("2b792cf786b0a73af536f77255af1324bc6c9a814b084100f606e7d2b83881f3"),
                ArtifactSetRegistry.OPEN_WOUNDS,
                Component.text("Wanted Poster"),
                Component.text("An old wanted poster with a hefty bounty award.")
        );
    }
    
}
