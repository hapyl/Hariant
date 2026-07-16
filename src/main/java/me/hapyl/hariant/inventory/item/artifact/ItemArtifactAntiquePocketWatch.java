package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSetRegistry;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ItemArtifactAntiquePocketWatch extends ItemArtifact {
    
    public ItemArtifactAntiquePocketWatch(@NotNull Key key) {
        super(
                key,
                Icon.ofTexture("b60eca715b31a8a7b1eb35cb7ade089ac56944ec7e0217ddadec6fe9bc4a766d"),
                ArtifactSetRegistry.REWIND,
                Component.text("Antique Pocket Watch"),
                Component.empty()
                         .append(Component.text("An old antique pocket watch whose hands haven't moved in a long time."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("But when the moment comes, it will strike its hour."))
        );
    }
    
}