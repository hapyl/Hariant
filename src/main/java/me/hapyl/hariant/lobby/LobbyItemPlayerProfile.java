package me.hapyl.hariant.lobby;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.menu.MenuPlayerProfile;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public final class LobbyItemPlayerProfile extends LobbyItemImpl {
    
    private static final String EXCLAMATION_TEXTURE = "51b826917d2da6d8d7516f30181a958c27503f949a0e5394975c13284e12fd";
    
    LobbyItemPlayerProfile() {
        super(5, Key.ofString("player_profile"), Material.PLAYER_HEAD, Component.text("Profile"), Component.text("Opens your personal profiles, which shows personal information."));
    }
    
    @NotNull
    @Override
    public ItemBuilder createBuilder(@NotNull PlayerProfile profile) {
        return super.createBuilder(profile).editMeta(SkullMeta.class, meta -> meta.setOwningPlayer(profile.getPlayer()));
    }
    
    public static void give(@NotNull PlayerProfile profile, int numberOfNotifications) {
        final EnumLobbyItem lobbyItem = EnumLobbyItem.PLAYER_PROFILE;
        final ItemBuilder builder = lobbyItem.createBuilder(profile);
        
        if (numberOfNotifications > 0) {
            builder.setHeadTexture(EXCLAMATION_TEXTURE);
            builder.setAmount(numberOfNotifications);
        }
        
        profile.getPlayer().getInventory().setItem(lobbyItem.getSlot(), builder.build());
    }
    
    @Override
    public void use(@NotNull Player player) {
        new MenuPlayerProfile(player);
    }
    
}