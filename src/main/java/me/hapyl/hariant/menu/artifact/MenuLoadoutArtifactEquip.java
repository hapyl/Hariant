package me.hapyl.hariant.menu.artifact;

import me.hapyl.eterna.module.inventory.menu.ChestSize;
import me.hapyl.hariant.inventory.item.artifact.loadout.ArtifactLoadout;
import me.hapyl.hariant.inventory.item.artifact.loadout.ArtifactLoadoutIndex;
import me.hapyl.hariant.menu.Menu;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MenuLoadoutArtifactEquip extends Menu {
    
    public MenuLoadoutArtifactEquip(@NotNull Player player, @NotNull ArtifactLoadoutIndex index, @Nullable ArtifactLoadout loadout) {
        super(player, Component::empty, ChestSize.SIZE_1);
    }
    
    @Override
    public void updateMenu() {
    }
    
}
