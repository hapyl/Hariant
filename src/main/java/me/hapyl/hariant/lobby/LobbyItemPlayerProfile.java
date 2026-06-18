package me.hapyl.hariant.lobby;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.profile.menu.MenuPlayerProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public final class LobbyItemPlayerProfile extends LobbyItemImpl {
    
    LobbyItemPlayerProfile() {
        super(5, Key.ofString("player_profile"), Material.PLAYER_HEAD, Component.text("Profile"), Component.text("Opens your personal profiles, which shows personal information."));
    }
    
    @NotNull
    @Override
    public ItemBuilder createBuilder(@NotNull Player player) {
        return super.createBuilder(player).editMeta(SkullMeta.class, meta -> meta.setOwningPlayer(player));
    }
    
    @Override
    public void use(@NotNull Player player) {
        new MenuPlayerProfile(player);
    }
    
}