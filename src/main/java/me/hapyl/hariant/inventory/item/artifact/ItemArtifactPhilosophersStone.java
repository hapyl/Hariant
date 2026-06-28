package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSetRegistry;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ItemArtifactPhilosophersStone extends ItemArtifact {
    
    public ItemArtifactPhilosophersStone(@NotNull Key key) {
        super(
                key,
                Icon.ofTexture("1cf948805dcb43a2cf0406aab37a412ceeff82a6ba1541a11d6c8307555d1aa6"),
                ArtifactSetRegistry.ALCHEMICAL_SYNERGY,
                Component.text("Philosopher's Stone"),
                Component.text("An alchemical substance resembling a stone, said to be capable of turning lead to gold.")
        );
    }
    
}