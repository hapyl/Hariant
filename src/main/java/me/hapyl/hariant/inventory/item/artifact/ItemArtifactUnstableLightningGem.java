package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSetRegistry;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ItemArtifactUnstableLightningGem extends ItemArtifact {
    
    public ItemArtifactUnstableLightningGem(@NotNull Key key) {
        super(
                key,
                Icon.ofTexture("2136e8a543621bb511e386507aeae4613928682649bd147d0f7939411cb49081"),
                ArtifactSetRegistry.ELECTRIFYING,
                Component.text("Unstable Lightning Gem"),
                Component.text("A crystal gem infused with unstable lightning energy, which seem to react to the touch.")
        );
    }
    
}
