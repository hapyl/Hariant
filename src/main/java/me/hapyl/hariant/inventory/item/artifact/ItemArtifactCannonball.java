package me.hapyl.hariant.inventory.item.artifact;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.inventory.item.artifact.set.ArtifactSetRegistry;
import me.hapyl.hariant.util.Icon;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ItemArtifactCannonball extends ItemArtifact {
    
    public ItemArtifactCannonball(@NotNull Key key) {
        super(
                key,
                Icon.ofTexture("22523e15e9986355a1f851f43f750ee3f23c89ae123631da241f872ba7a781"),
                ArtifactSetRegistry.GLASS_CANNON,
                Component.text("Cannonball"),
                Component.empty()
                         .append(Component.text("A round, solid metal ball, meant to be used as ammunition."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Why exactly do you need it for?"))
        );
    }
    
}
