package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSetRegistry;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ItemArtifactInfernalCrucible extends ItemArtifact {
    
    public ItemArtifactInfernalCrucible(@NotNull Key key) {
        super(
                key,
                Icon.ofTexture("a712bb9649cb2ddfe9eb5052165b5c75704bf465b0fd6558724c9ac54e52bb49"),
                ArtifactSetRegistry.SEARING_INFERNO,
                Component.text("Infernal Crucible"),
                Component.text("A piece of forever burning infernal crucible that should burn hot, yet it doesn't.")
        );
    }
    
}