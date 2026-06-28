package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSetRegistry;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ItemArtifactVoidRift extends ItemArtifact {
    
    public ItemArtifactVoidRift(@NotNull Key key) {
        super(
                key,
                Icon.ofTemporaryTexture(),
                ArtifactSetRegistry.ECLIPSE,
                Component.text("Void Rift"),
                Component.text("A crystallized Ætheric matter that seems to enhance ones abilities.")
        );
    }
    
}