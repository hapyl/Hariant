package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSetRegistry;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ItemArtifactZealotMedallion extends ItemArtifact {
    
    public ItemArtifactZealotMedallion(@NotNull Key key) {
        super(
                key,
                Icon.ofTexture("9c7fb6f414ff0eb6bf5797acb3b3af354ca3af95ceb8f3418125f6b805419f39"),
                ArtifactSetRegistry.SWORN_OATH,
                Component.text("Zealots' Medallion"),
                Component.text("A medallion used by the Zealots to harness power of the cosmos.")
        );
    }
    
}
