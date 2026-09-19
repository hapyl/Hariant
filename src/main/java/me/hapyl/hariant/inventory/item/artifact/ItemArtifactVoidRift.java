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
                Icon.ofTexture("d95784d518f601d62db289dfdf9f80595d2fafca760712eb9278afe3784082d7"),
                ArtifactSetRegistry.ECLIPSE,
                Component.text("Void Rift"),
                Component.text("A crystallized Ætheric matter that seems to enhance ones abilities.")
        );
    }
    
}