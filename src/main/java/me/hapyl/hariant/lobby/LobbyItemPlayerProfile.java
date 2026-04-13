package me.hapyl.hariant.lobby;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.HariantLogger;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public class LobbyItemPlayerProfile extends LobbyItemImpl {
    
    LobbyItemPlayerProfile(@NotNull Key key) {
        super(
                key,
                Material.PLAYER_HEAD,
                Component.text("Profile"),
                Component.text("Opens your personal profiles, which shows personal information.")
        );
    }
    
    @NotNull
    @Override
    public ItemBuilder createBuilder(@NotNull Player player) {
        return super.createBuilder(player).editMeta(SkullMeta.class, meta -> meta.setOwningPlayer(player));
    }
    
    @Override
    public void use(@NotNull Player player) {
        HariantLogger.error(player, Component.text("I'm going to be honest with you, I haven't yet implemented player profiles yet, so pretend a fancy menu opened with all your cool data."));
    }
}
