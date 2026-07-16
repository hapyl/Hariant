package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSetRegistry;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ItemArtifactShatteredSoul extends ItemArtifact {
    
    public ItemArtifactShatteredSoul(@NotNull Key key) {
        super(
                key,
                Icon.ofTexture("932a6f27680e38d3c6f63f70a28b4130817bd99c6126e6c9285a3d9c82afa1c9"),
                ArtifactSetRegistry.SOUL_FRACTURE,
                Component.text("Shattered Soul"),
                Component.text("A shattered soul that seem to be stuck in time, trying to escape, but never succeeds.")
        );
    }
    
}
