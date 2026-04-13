package me.hapyl.hariant.lobby;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.menu.hero.MenuHeroSelection;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LobbyItemHeroSelector extends LobbyItemImpl {
    
    LobbyItemHeroSelector(@NotNull Key key) {
        super(key, Material.TOTEM_OF_UNDYING, Component.text("Hero Selector"), Component.text("Select, preview or unlocks heroes."));
    }
    
    @Override
    public void use(@NotNull Player player) {
        new MenuHeroSelection(player);
    }
}
