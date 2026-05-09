package me.hapyl.hariant.handler;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.hariant.HariantLogger;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;
import java.util.Map;

public final class PlayerSitHandler implements Listener {
    
    private final Map<Entity, Block> occupiedBeds = Maps.newHashMap();
    
    @EventHandler
    public void handlePlayerInteractEvent(PlayerInteractEvent ev) {
        final Player player = ev.getPlayer();
        
        if (ev.getHand() == EquipmentSlot.OFF_HAND || ev.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        final Block clickedBlock = ev.getClickedBlock();
        
        if (clickedBlock == null) {
            return;
        }
        
        if (!(clickedBlock.getBlockData() instanceof Bed bed)) {
            return;
        }
        
        if (bed.getPart() != Bed.Part.FOOT) {
            return;
        }
        
        // Check whether the bed is occupied
        if (occupiedBeds.containsValue(clickedBlock)) {
            HariantLogger.error(player, Component.text("This block is already occupied!"));
            return;
        }
        
        // Sit on the bed
        sit(player, clickedBlock, bed);
    }
    
    @EventHandler
    public void handleEntityDismountEvent(EntityDismountEvent ev) {
        if (ev.getDismounted() instanceof ArmorStand armorStand) {
            dismount(armorStand);
        }
    }
    
    @EventHandler
    public void handlePlayerQuitEvent(PlayerQuitEvent ev) {
        final Player player = ev.getPlayer();
        final Entity vehicle = player.getVehicle();
        
        if (vehicle instanceof ArmorStand armorStand) {
            dismount(armorStand);
        }
    }
    
    public void dismount(@NotNull ArmorStand vehicle) {
        if (!occupiedBeds.containsKey(vehicle)) {
            return;
        }
        
        final List<Entity> passengers = vehicle.getPassengers();
        final Block block = occupiedBeds.remove(vehicle);
        
        vehicle.remove();
        
        // If the block isn't bed, return; which can't happen, but a better check
        if (!(block.getBlockData() instanceof Bed bed)) {
            return;
        }
        
        // Always dismount in front of the bed
        final Location location = block.getLocation().add(0.5, 0, 0.5).add(bed.getFacing().getDirection().multiply(-1));
        
        passengers.forEach(passenger -> {
            // Merge entity yaw and pitch
            location.setYaw(passenger.getYaw());
            location.setPitch(passenger.getPitch());
            
            passenger.teleport(location);
        });
    }
    
    public void sit(@NotNull Player player, @UnknownNullability Block block, @NotNull Bed bed) {
        final Location location = block.getLocation();
        final float yaw = blockFaceToInvertYaw(bed.getFacing());
        
        location.add(0.5, 0.65, 0.5);
        location.setYaw(yaw);
        
        final ArmorStand armorStand = Entities.ARMOR_STAND.spawn(location, self -> {
            self.setMarker(true);
            self.setSilent(true);
            self.setInvisible(true);
            self.setInvulnerable(true);
        });
        
        armorStand.addPassenger(player);
        occupiedBeds.put(armorStand, block);
        
        // Fx
        PlayerLib.playSound(player.getLocation(), Sound.ENTITY_HORSE_SADDLE, 1.75f);
    }
    
    private float blockFaceToInvertYaw(@NotNull BlockFace face) {
        return switch (face) {
            case NORTH -> 0f;
            case SOUTH -> 180f;
            case EAST -> 90f;
            case WEST -> 270f;
            default -> 0.0f;
        };
    }
}
