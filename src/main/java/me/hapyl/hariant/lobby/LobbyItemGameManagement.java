package me.hapyl.hariant.lobby;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.menu.MenuGameManagement;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class LobbyItemGameManagement extends LobbyItemImpl {
    
    LobbyItemGameManagement() {
        super(3, Key.ofString("game_management"), Material.WRITTEN_BOOK, Component.text("Game Management"), Component.text("Manage the game settings."));
    }
    
    @Override
    public void use(@NotNull Player player) {
        new MenuGameManagement(player);
    }
    
}