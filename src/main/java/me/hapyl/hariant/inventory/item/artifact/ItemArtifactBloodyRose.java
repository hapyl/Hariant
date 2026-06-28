package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSetRegistry;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ItemArtifactBloodyRose extends ItemArtifact {
    
    public ItemArtifactBloodyRose(@NotNull Key key) {
        super(
                key,
                Icon.ofTexture("676f0a8f03a79e4b19bef283b8915e1ffb0c42b311530b01550b31893d4b0742"),
                ArtifactSetRegistry.BREEZE,
                Component.text("Bloody Rose"),
                Component.text("A white rose drenched in hot, red blood.")
        );
    }
    
}
