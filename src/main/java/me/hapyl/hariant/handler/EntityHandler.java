package me.hapyl.hariant.handler;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.cooldown.Cooldown;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.damage.EnvironmentDamageSupplier;
import me.hapyl.hariant.entity.player.HariantPlayer;
import org.bukkit.GameMode;
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
                EnvironmentDamageSupplier.entry(
                        org.bukkit.damage.DamageType.CACTUS,
                        ElementType.PHYSICAL,
                        DeathMessage.createWithDefaultKiller("{player} was prickled to death"),
                        50
                ),
                
                EnvironmentDamageSupplier.entry(
                        org.bukkit.damage.DamageType.CAMPFIRE,
                        ElementType.FIRE,
                        DeathMessage.createWithDefaultKiller("{player} burnt to death"),
                        50
                ),
                
                EnvironmentDamageSupplier.entry(
                        org.bukkit.damage.DamageType.DROWN,
                        ElementType.WATER,
                        DeathMessage.createWithDefaultKiller("{player} drowned"),
                        50
                ),
                
                EnvironmentDamageSupplier.entry(
                        org.bukkit.damage.DamageType.EXPLOSION,
                        ElementType.PHYSICAL,
                        DeathMessage.createWithDefaultKiller("{player} exploded to death"),
                        250
                ),
                
                EnvironmentDamageSupplier.entry(
                        org.bukkit.damage.DamageType.FREEZE,
                        ElementType.ICE,
                        DeathMessage.createWithDefaultKiller("{player} froze to death"),
                        50
                ),
                
                EnvironmentDamageSupplier.entry(
                        org.bukkit.damage.DamageType.HOT_FLOOR,
                        ElementType.FIRE,
                        DeathMessage.createWithDefaultKiller("{player} had their toes burnt"),
                        50
                ),
                
                EnvironmentDamageSupplier.entry(
                        org.bukkit.damage.DamageType.IN_FIRE,
                        ElementType.FIRE,
                        DeathMessage.createWithDefaultKiller("{player} burnt to a crisp"),
                        50
                ),
                
                EnvironmentDamageSupplier.entry(
                        org.bukkit.damage.DamageType.IN_WALL,
                        ElementType.PHYSICAL,
                        DeathMessage.createWithDefaultKiller("{player} suffocated"),
                        50
                ),
                
                EnvironmentDamageSupplier.entry(
                        org.bukkit.damage.DamageType.LAVA,
                        ElementType.FIRE,
                        DeathMessage.create("{player} tried to swim in lava [while running from {killer}]"),
                        100
                ),
                
                EnvironmentDamageSupplier.entry(
                        org.bukkit.damage.DamageType.ON_FIRE,
                        ElementType.FIRE,
                        DeathMessage.createWithDefaultKiller("{player} burnt to death"),
                        10
                ),
                
                EnvironmentDamageSupplier.entry(
                        org.bukkit.damage.DamageType.GENERIC_KILL,
                        ElementType.PHYSICAL,
                        DeathMessage.create("{player} was killed [by {killer}]"),
                        1_000_000
                ),
                
                Map.entry(org.bukkit.damage.DamageType.FALL, EnvironmentDamageSupplier.fallDamage())
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
            private static final Cooldown INTERACTION_COOLDOWN = Cooldown.ofTicks(Key.ofString("interaction_cooldown"), 5);
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
