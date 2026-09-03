package me.hapyl.hariant.handler;

import com.google.common.collect.Maps;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.entity.damage.DamageFlag;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.event.HariantProjectileHitEvent;
import me.hapyl.hariant.event.HariantProjectileLaunchEvent;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * Projectiles are VERY annoying to deal with, since we have to store the damage source on them,
 * therefore we wrap them on launch in a {@link HariantProjectile}, that default to using a {@link NormalAttack}
 * damage source, which can be overriden by an event.
 */
public final class ProjectileHandler implements Listener {
    
    private static final Map<Projectile, HariantProjectile> PROJECTILES = Maps.newHashMap();
    private static final Set<? extends DamageFlag> FORCE_CRITICAL = Set.of(DamageFlag.FORCE_CRITICAL);
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleProjectileLaunchEvent(ProjectileLaunchEvent ev) {
        final Projectile projectile = ev.getEntity();
        final ProjectileSource shooter = projectile.getShooter();
        
        // If the wrapper is already registered, return
        if (PROJECTILES.containsKey(projectile)) {
            return;
        }
        
        final @Nullable HariantEntity entity = shooter instanceof LivingEntity livingShooter ? Hariant.getEntity(livingShooter).orElse(null) : null;
        
        // If shooter is null, it means either non-game entity or lobby player, don't care either way nor wrap projectile
        if (entity == null) {
            return;
        }
        
        // Otherwise we get the entity normal attack scaling
        final NormalAttack rangedAttack = entity.getRangedAttack();
        
        // Ignore if entity has no ranged damage, it should have used launchProjectile()!
        if (rangedAttack == null) {
            return;
        }
        
        final DamageSource.Builder builder = rangedAttack.createDamageSource(entity);
        
        // If the project is an arrow, and it's fully charged (critical), add `FORCE_CRITICAL` tag
        if (projectile instanceof Arrow arrow && arrow.isCritical()) {
            builder.damageFlags(FORCE_CRITICAL, DamageSource.Strategy.MERGE);
        }
        
        // Create natural projectile
        createProjectile(projectile, builder.build(), HariantProjectileNatural::new);
        
        // Call `onShoot` for naturally shot weapons
        entity.onShoot();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleProjectileHitEvent(ProjectileHitEvent ev) {
        final Projectile bukkitProjectile = ev.getEntity();
        final HariantProjectile projectile = PROJECTILES.remove(bukkitProjectile);
        
        // Always remove projectiles
        bukkitProjectile.remove();
        
        if (projectile == null) {
            return;
        }
        
        final Entity bukkitEntity = ev.getHitEntity();
        final HariantEntity entity = bukkitEntity != null ? Hariant.getEntity(bukkitEntity).orElse(null) : null;
        final Block block = ev.getHitBlock();
        
        if (new HariantProjectileHitEvent(projectile, entity, block).callEvent()) {
            ev.setCancelled(true);
            return;
        }
        
        projectile.onHit(entity, block);
    }
    
    public static <P extends Projectile, H extends HariantProjectile> void createProjectile(@NotNull P projectile, @NotNull DamageSource damageSource, @NotNull ProjectileConstructor<P, H> constructor) {
        final H hariantProjectile = constructor.construct(projectile, damageSource);
        
        // Call projectile launch event
        if (new HariantProjectileLaunchEvent(hariantProjectile).callEvent()) {
            projectile.remove();
            return;
        }
        
        PROJECTILES.put(projectile, hariantProjectile);
        hariantProjectile.onLaunch();
    }
    
    public static void playHitSound(@NotNull HariantEntity entity) {
        entity.playSound(Sound.ENTITY_ARROW_HIT_PLAYER, 1.0f);
    }
    
    private static class HariantProjectileNatural extends HariantProjectile {
        
        HariantProjectileNatural(@NotNull Projectile projectile, @NotNull DamageSource damageSource) {
            super(projectile, damageSource);
        }
        
        @Override
        public void onHit(@Nullable HariantEntity entity, @Nullable Block block) {
            final HariantEntity shooter = this.getShooter();
            
            if (entity != null) {
                // Natural projectiles deal damage via `attack`, instead of `damage`
                shooter.attack(entity, this.getDamageSource(), this.createKnockbackSource());
                
                playHitSound(shooter);
            }
        }
        
    }
    
}
