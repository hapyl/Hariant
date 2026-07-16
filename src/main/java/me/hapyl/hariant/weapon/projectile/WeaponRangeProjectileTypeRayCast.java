package me.hapyl.hariant.weapon.projectile;

import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.NormalAttack;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.handler.ProjectileHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class WeaponRangeProjectileTypeRayCast extends WeaponRangeProjectileType {
    
    public WeaponRangeProjectileTypeRayCast(@NotNull Component name, @NotNull Component description) {
        super(name, description);
    }
    
    @Override
    public void onHitEntity(@NotNull HariantPlayer player, @NotNull WeaponRangeProjectile weapon, @NotNull HariantEntity entity) {
    }
    
    @Override
    public void onHitBlock(@NotNull HariantPlayer player, @NotNull WeaponRangeProjectile weapon, @NotNull Block block) {
    }
    
    @Override
    public void onShoot(@NotNull HariantPlayer player, @NotNull WeaponRangeProjectile weapon) {
    }
    
    @Override
    public void onTravel(@NotNull HariantPlayer player, @NotNull WeaponRangeProjectile weapon, @NotNull Location location) {
    }
    
    @Override
    public void launch(@NotNull HariantPlayer player, @NotNull WeaponRangeProjectile weapon) {
        final Location location = player.getEyeLocation();
        final Vector vector = location.getDirection().normalize();
        
        this.onShoot(player, weapon);
        
        for (double distance = 0; distance < projectileDistance; distance += projectileStep) {
            final double x = vector.getX() * distance;
            final double y = vector.getY() * distance;
            final double z = vector.getZ() * distance;
            
            location.add(x, y, z);
            
            // Call `onTravel`
            this.onTravel(player, weapon, location);
            
            // Check for block collision
            final Block block = location.getBlock();
            
            if (!collisionMode.canPassThrough(block)) {
                this.onHitBlock(player, weapon, block);
                return;
            }
            
            // Check for entity collision
            final HariantEntity hitEntity = player.collectNearbyEntities(location, projectileRadius)
                                                  .filter(player::canAffect)
                                                  .findAny()
                                                  .orElse(null);
            
            if (hitEntity != null) {
                // Deal damage to the entity
                final NormalAttack rangedAttack = weapon.getRangedAttack();
                
                hitEntity.damage(
                        rangedAttack.createDamageSource(player)
                                    .damageType(DamageType.RANGED)
                                    .build()
                );
                
                // Apply knockback
                hitEntity.knockback(rangedAttack.createKnockbackCause(player));
                
                // Call `onHitEntity`
                this.onHitEntity(player, weapon, hitEntity);
                ProjectileHandler.playHitSound(player);
                return;
            }
            
            location.subtract(x, y, z);
        }
        
    }
    
}
