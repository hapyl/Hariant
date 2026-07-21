package me.hapyl.hariant.game.booster;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.entity.cooldown.HariantCooldown;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.util.ImmutableLocation;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Map;

public final class BoosterHandler implements Listener {
    
    static final Map<ImmutableLocation, Booster> BOOSTERS = Maps.newHashMap();
    
    private static final HariantCooldown BOOSTER_COOLDOWN = HariantCooldown.ofSeconds(Key.ofString("booster_cooldown"), 1);
    
    @EventHandler
    public void handlePlayerInteractEvent(PlayerInteractEvent ev) {
        if (ev.getHand() == EquipmentSlot.OFF_HAND || ev.getAction() != Action.PHYSICAL) {
            return;
        }
        
        final Block clickedBlock = ev.getClickedBlock();
        
        if (clickedBlock == null) {
            return;
        }
        
        final HariantPlayer player = Hariant.getPlayer(ev.getPlayer()).orElse(null);
        
        if (player == null) {
            return;
        }
        
        final Booster booster = BOOSTERS.get(ImmutableLocation.create(clickedBlock.getLocation()));
        
        if (booster == null) {
            return;
        }
        
        ev.setCancelled(true);
        
        // If on cooldown, return
        if (player.hasCooldown(BOOSTER_COOLDOWN)) {
            return;
        }
        
        booster.boost(player);
        player.setCooldown(BOOSTER_COOLDOWN);
    }
    
}
