package me.hapyl.hariant.lobby;

import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
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
        
        // Have to send the message before toggling the ready status because it will send messages that appear before ready notification
        HariantLogger.PREFIX_INFO.broadcastMessage(
                Component.empty()
                         .append(profile.getNameFormatted())
                         .appendSpace()
                         .append(
                                 ready
                                 ? Component.text("is now ready.", Colors.SUCCESS)
                                 : Component.text("is no longer ready.", Colors.ERROR)
                         )
        );
        
        PlayerLib.playSound(Sound.BLOCK_NOTE_BLOCK_HAT, ready ? 0.75f : 0.5f);
        
        profile.setReady(ready);
    }
    
    @Override
    public @NotNull ItemBuilder createBuilder(@NotNull Player player) {
        return super.createBuilder(player)
                    .setType(Hariant.getPlayerProfile(player).isReady() ? MATERIAL_READY : MATERIAL_NOT_READY);
    }
}