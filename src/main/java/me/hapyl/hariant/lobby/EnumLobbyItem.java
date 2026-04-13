package me.hapyl.hariant.lobby;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public enum EnumLobbyItem implements LobbyItem {
    
    HERO_SELECTOR(2, new LobbyItemHeroSelector(Key.ofString("hero_selector"))),
    PLAYER_PROFILE(4, new LobbyItemPlayerProfile(Key.ofString("player_profile"))),
    
    
    ;
    
    private final int slot;
    private final LobbyItem lobbyItem;
    
    EnumLobbyItem(int slot, @NotNull LobbyItem lobbyItem) {
        this.slot = slot;
        this.lobbyItem = lobbyItem;
    }
    
    @NotNull
    @Override
    public Component getName() {
        return lobbyItem.getName();
    }
    
    @NotNull
    @Override
    public Component getDescription() {
        return lobbyItem.getDescription();
    }
    
    @NotNull
    @Override
    public ItemBuilder createBuilder(@NotNull Player player) {
        return lobbyItem.createBuilder(player);
    }
    
    @Override
    public void use(@NotNull Player player) {
        lobbyItem.use(player);
    }
    
    public static void giveAll(@NotNull Player player) {
        final PlayerInventory inventory = player.getInventory();
        inventory.clear();
        
        for (EnumLobbyItem lobbyItem : EnumLobbyItem.values()) {
            inventory.setItem(lobbyItem.slot, lobbyItem.createBuilder(player).build());
        }
    }
    
}
