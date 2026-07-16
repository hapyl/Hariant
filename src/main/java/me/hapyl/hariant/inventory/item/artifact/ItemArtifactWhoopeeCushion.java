package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSetRegistry;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ItemArtifactWhoopeeCushion extends ItemArtifact {
    
    public ItemArtifactWhoopeeCushion(@NotNull Key key) {
        super(
                key,
                Icon.ofTexture("f2f8859a07fdbec3072dd8c6af492d3e6176c4890d64e01b630aa7a26c8ba536"),
                ArtifactSetRegistry.FUNNY_PRANK,
                Component.text("Whoopee Cushion"),
                Component.empty()
                         .append(Component.text("An item famous for making a funny sound whenever someone sits on it."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("For unknown reasons, it brings luck."))
        );
    }
    
}