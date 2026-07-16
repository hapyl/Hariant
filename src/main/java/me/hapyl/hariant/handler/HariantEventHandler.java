package me.hapyl.hariant.handler;

import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroInstance;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.jetbrains.annotations.NotNull;

public interface HariantEventHandler {
    
    void handleItemHeldEvent(@NotNull HariantPlayer player, @NotNull HeroInstance heroInstance, @NotNull PlayerItemHeldEvent ev);
    
    void handleSwapHandItemsEvent(@NotNull HariantPlayer player, @NotNull HeroInstance heroInstance, @NotNull PlayerSwapHandItemsEvent ev);
    
}
