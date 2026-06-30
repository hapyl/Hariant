package me.hapyl.hariant.lobby;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class LobbyItemReady extends LobbyItemImpl {
    
    private static final Material MATERIAL_READY = Material.LIME_DYE;
    private static final Material MATERIAL_NOT_READY = Material.GRAY_DYE;
    
    LobbyItemReady() {
        super(7, Key.ofString("lobby_ready"), MATERIAL_NOT_READY, Component.text("Ready"), Component.text("Toggles ready status or cancels countdown."));
        
        this.cooldown = 20;
    }
    
    @Override
    public void use(@NotNull Player player) {
        final PlayerProfile profile = Hariant.getPlayerProfile(player);
        final boolean ready = !profile.isReady();
        
        profile.setReady(ready);
    }
    
    @Override
    public @NotNull ItemBuilder createBuilder(@NotNull Player player) {
        return super.createBuilder(player)
                    .setType(Hariant.getPlayerProfile(player).isReady() ? MATERIAL_READY : MATERIAL_NOT_READY);
    }
}