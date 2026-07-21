package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSetRegistry;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ItemArtifactCuirass extends ItemArtifact {
    public ItemArtifactCuirass(@NotNull Key key) {
        super(
                key,
                Icon.ofTemporaryTexture(),
                ArtifactSetRegistry.BULWARK,
                Component.text("Kingdom's Cuirass"),
                Component.text("A piece of advanced heavy armor issued by the Kingdom.")
        );
    }
    
}