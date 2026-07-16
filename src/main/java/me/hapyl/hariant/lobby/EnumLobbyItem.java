package me.hapyl.hariant.lobby;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public enum EnumLobbyItem implements LobbyItem {
    
    HERO_SELECTOR(new LobbyItemHeroSelector()),
    GAME_MANAGEMENT(new LobbyItemGameManagement()),
    PLAYER_PROFILE(new LobbyItemPlayerProfile()),
    READY(new LobbyItemReady());
    
    private final LobbyItem lobbyItem;
    
    EnumLobbyItem(@NotNull LobbyItem lobbyItem) {
        this.lobbyItem = lobbyItem;
    }
    
    @Override
    public int getSlot() {
        return lobbyItem.getSlot();
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
    
    @Override
    public void give(@NotNull Player player) {
        lobbyItem.give(player);
    }
    
    public static void clearInventoryAndGiveAllItems(@NotNull Player player) {
        final PlayerInventory inventory = player.getInventory();
        inventory.clear();
        
        for (EnumLobbyItem lobbyItem : EnumLobbyItem.values()) {
            lobbyItem.give(player);
        }
    }
    
}
