package me.hapyl.hariant.handler;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.cooldown.HariantCooldown;
import me.hapyl.hariant.entity.damage.environment.*;
import me.hapyl.hariant.entity.player.HariantPlayer;
import org.bukkit.GameMode;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Map;

public final class EntityHandler implements Listener {
    
    private final Map<org.bukkit.damage.DamageType, EnvironmentDamageSupplier> environmentDamageMap;
    
    public EntityHandler() {
        this.environmentDamageMap = Map.ofEntries(
                Map.entry(DamageType.CACTUS, entity -> new EnvironmentDamageSourceCactus()),
                Map.entry(DamageType.CAMPFIRE, entity -> new EnvironmentDamageSourceCampfire()),
                Map.entry(DamageType.DROWN, entity -> new EnvironmentDamageSourceDrown()),
                Map.entry(DamageType.EXPLOSION, entity -> new EnvironmentDamageSourceExplosion()),
                Map.entry(DamageType.FREEZE, entity -> new EnvironmentDamageSourceFreeze()),
                Map.entry(DamageType.HOT_FLOOR, entity -> new EnvironmentDamageSourceHotFloor()),
                Map.entry(DamageType.IN_FIRE, entity -> new EnvironmentDamageSourceInFire()),
                Map.entry(DamageType.IN_WALL, entity -> new EnvironmentDamageSourceInWall()),
                Map.entry(DamageType.LAVA, entity -> new EnvironmentDamageSourceLava()),
                Map.entry(DamageType.ON_FIRE, entity -> new EnvironmentDamageSourceOnFire()),
                Map.entry(DamageType.GENERIC_KILL, entity -> new EnvironmentDamageSourceGenericKill()),
                Map.entry(DamageType.FALL, EnvironmentDamageSourceFall.createSupplier())
        );
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleEntityDamageEvent(EntityDamageEvent ev) {
        // We always cancel vanilla damage
        ev.setCancelled(true);
        
        // If hit by a creative player, remove the entity
        final Entity bukkitEntity = ev.getEntity();
        final HariantEntity entity = Hariant.getEntity(bukkitEntity).orElse(null);
        
        if (ev instanceof EntityDamageByEntityEvent ev2 && ev2.getDamager() instanceof Player player && player.getGameMode() == GameMode.CREATIVE && !(bukkitEntity instanceof Player)) {
            // If HariantEntity exists, call removal on that so it clears stuff, otherwise, remove bukkit entity
            if (entity != null) {
                entity.remove();
            }
            else {
                bukkitEntity.remove();
            }
            return;
        }
        
        // If the entity that took damage isn't wrapped, it either means wrongly spawned entity or
        // player isn't in the game, either way we don't care
        if (entity == null) {
            return;
        }
        
        final org.bukkit.damage.DamageSource damageSource = ev.getDamageSource();
        final org.bukkit.damage.DamageType damageType = damageSource.getDamageType();
        
        // Check for environment damage
        final EnvironmentDamageSupplier environmentDamageSupplier = environmentDamageMap.get(damageType);
        
        if (environmentDamageSupplier != null) {
            entity.damage(environmentDamageSupplier.supply(entity));
        }
        else {
            final Entity directEntity = damageSource.getDirectEntity();
            final Entity causingEntity = damageSource.getCausingEntity();
            
            // Projectile damage needs special needs, so it's handled in ProjectileHandler
            if (directEntity instanceof Projectile) {
                return;
            }
            
            // Otherwise handle as normal attack
            final HariantEntity attacker = causingEntity != null ? Hariant.getEntity(causingEntity).orElse(null) : null;
            
            if (attacker != null) {
                attacker.attack(entity);
            }
        }
    }
    
    @EventHandler
    public void handleHangingEvent(HangingBreakEvent ev) {
        // If removed by an entity, check for the remover
        if (ev instanceof HangingBreakByEntityEvent ev2) {
            final Entity remover = ev2.getRemover();
            
            // If remover isn't a player or the player is not in creative mode, cancel the removal
            if (!(remover instanceof Player player) || player.getGameMode() != GameMode.CREATIVE) {
                ev.setCancelled(true);
            }
        }
        // Otherwise, always cancel removal
        else {
            ev.setCancelled(true);
        }
    }
    
    @EventHandler
    public void handleSlimeSplitEvent(SlimeSplitEvent ev) {
        ev.setCancelled(true);
        ev.setCount(0);
    }
    
    @EventHandler
    public void handlePlayerInteractEvent(PlayerInteractEntityEvent ev) {
        class Holder {
            private static final HariantCooldown INTERACTION_COOLDOWN = HariantCooldown.ofTicks(Key.ofString("interaction_cooldown"), 5);
        }
        
        if (ev.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }
        
        final HariantPlayer player = Hariant.getPlayer(ev.getPlayer()).orElse(null);
        
        if (player == null || player.hasCooldown(Holder.INTERACTION_COOLDOWN)) {
            return;
        }
        
        Hariant.getEntity(ev.getRightClicked()).ifPresent(entity -> {
            entity.onInteract(player);
            
            player.setCooldown(Holder.INTERACTION_COOLDOWN);
        });
    }
    
}
