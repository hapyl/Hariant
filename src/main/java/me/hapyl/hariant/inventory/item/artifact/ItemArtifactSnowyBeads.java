package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSetRegistry;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ItemArtifactSnowyBeads extends ItemArtifact {
    
    public ItemArtifactSnowyBeads(@NotNull Key key) {
        super(
                key,
                Icon.ofTexture("ba254d171c3eac611049e623d567a28691101b28714e6c3f73b132789afb11da"),
                ArtifactSetRegistry.PROOF_OF_POWER,
                Component.text("Snowy Beads"),
                Component.text("Magical beads that display the stature and power of the wearer.")
        );
    }
    
}
