package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSetRegistry;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ItemArtifactGreatWhiteSharkTooth extends ItemArtifact {
    
    public ItemArtifactGreatWhiteSharkTooth(@NotNull Key key) {
        super(
                key,
                Icon.ofTexture("f0c84320f4b0fcd8391b8a20f53bd41612ca6a50e758309525971f77ace2d"),
                ArtifactSetRegistry.BLOODSCENT,
                Component.text("Great White Shark's Tooth"),
                Component.text("A massive tooth that used to belong to a great white shark.")
        );
    }
    
}
