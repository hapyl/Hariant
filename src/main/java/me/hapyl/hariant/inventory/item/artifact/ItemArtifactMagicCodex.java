package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSetRegistry;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ItemArtifactMagicCodex extends ItemArtifact {
    public ItemArtifactMagicCodex(@NotNull Key key) {
        super(
                key,
                Icon.ofTexture("4dd5f059d22d7dd0bea5addaa8ea4a2323f3607c74c877b59489112ad60a5517"),
                ArtifactSetRegistry.TOME_OF_THE_ENLIGHTENED,
                Component.text("Magic Codex"),
                Component.text("A collection of novice magic lessons approved by the Kingdom.")
        );
    }
    
}
