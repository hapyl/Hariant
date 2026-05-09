package me.hapyl.hariant.npc;

import me.hapyl.hariant.registry.StaticRegistry;
import me.hapyl.hariant.registry.StaticRegistryMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public final class NpcHandler extends StaticRegistry<HariantNpc> implements Listener {
    
    public static final HariantNpc MR_NERD;
    
    private static final StaticRegistryMap<HariantNpc> REGISTRY;
    
    static {
        REGISTRY = requestRegistry(NpcHandler.class);
        
        MR_NERD = REGISTRY.register("mr_nerd", HariantNpcMrNerd::new);
    }
    
    @NotNull
    public static StaticRegistryMap<HariantNpc> getRegistry() {
        return REGISTRY;
    }
    
    @EventHandler
    public void handlePlayerJoinEvent(PlayerJoinEvent ev) {
        final Player player = ev.getPlayer();
        
        REGISTRY.values().forEach(npc -> {
            if (npc.shouldCreate(player)) {
                npc.show(player);
            }
        });
    }
    
    @EventHandler
    public void handlePlayerQuitEvent(PlayerQuitEvent ev) {
        final Player player = ev.getPlayer();
        
        REGISTRY.values().forEach(npc -> {
            npc.hide(player);
        });
    }
}
