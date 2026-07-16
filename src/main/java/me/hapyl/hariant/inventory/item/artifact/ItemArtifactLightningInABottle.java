package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSetRegistry;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ItemArtifactLightningInABottle extends ItemArtifact {
    
    public ItemArtifactLightningInABottle(@NotNull Key key) {
        super(
                key,
                Icon.ofTexture("f0f2185c51b6d7cee7e17042de2ddf8a448552af7b4af1804986fbdd09e9bc08"),
                ArtifactSetRegistry.RECHARGE,
                Component.text("Lightning In a Bottle"),
                Component.text("A lightning trapped in a bottle that apparently can be used as a quick energy refill.")
        );
    }
    
}