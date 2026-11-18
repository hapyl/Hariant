package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSetRegistry;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ItemArtifactVoodooDoll extends ItemArtifact {
    public ItemArtifactVoodooDoll(@NotNull Key key) {
        super(
                key,
                Icon.ofTexture("dc5aff69a699892947969c735f7306d18acb0617515cc7b44914cd16d8820d52"),
                ArtifactSetRegistry.ANCESTRAL_BLOOM,
                Component.text("Voodoo Doll"),
                Component.empty()
                         .append(Component.text("A human shaped doll with needles poking out of it."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("It is an inanimate object, but it as if you can feel its pain."))
        );
    }
}
