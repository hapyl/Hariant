package me.hapyl.hariant.lobby;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.player.PlayerAction;
import me.hapyl.hariant.util.SlotBound;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface LobbyItem extends SlotBound, Named, Described, PlayerAction {
    
    @Override
    int getSlot();
    
    @Override
    @NotNull
    Component getName();
    
    @NotNull
    @Override
    Component getDescription();
    
    @NotNull
    ItemBuilder createBuilder(@NotNull Player player);
    
    @Override
    void use(@NotNull Player player);
    
    void give(@NotNull Player player);
    
}
