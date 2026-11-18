package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSetRegistry;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ItemArtifactHourglass extends ItemArtifact {
    
    public ItemArtifactHourglass(@NotNull Key key) {
        super(
                key,
                Icon.ofTexture("b778020c58cd86d495124e4138af7813c20eb64d69b5e169bd1ab7bbd1453672"),
                ArtifactSetRegistry.TIME_SLIP,
                Component.text("Hourglass of Afterglow"),
                Component.empty()
                         .append(Component.text("An hourglass made of antique materials."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("The sand falls upward when no one is looking."))
        );
    }
    
}
