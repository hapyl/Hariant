package me.hapyl.hariant.talent.target;

import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.util.BlockHelper;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public final class TalentTargetEntityRayCast implements TalentTarget {
    
    private final double maxDistance;
    private final double lookupRadius;
    
    private final BlockCollision collision;
    private final EntityPriority priority;
    
    private final Predicate<HariantEntity> filter;
    
    TalentTargetEntityRayCast(double maxDistance, double lookupRadius, @NotNull BlockCollision collision, @NotNull EntityPriority priority, @NotNull Predicate<HariantEntity> filter) {
        this.maxDistance = maxDistance;
        this.lookupRadius = lookupRadius;
        this.collision = collision;
        this.priority = priority;
        this.filter = filter;
    }
    
    @Override
    public @Nullable TalentContext createContext(@NotNull HariantPlayer player) {
        final Location location = player.getEyeLocation();
        final World world = location.getWorld();
        final Vector vector = location.getDirection().normalize();
        
        HariantEntity target = null;
        
        for (double d = 0; d < maxDistance; d += 0.5) {
            final double x = vector.getX() * d;
            final double y = vector.getY() * d;
            final double z = vector.getZ() * d;
            
            location.add(x, y, z);
            
            // If collision with the block is not allowed, break out the loop
            if (!collision.test(location.getBlock())) {
                break;
            }
            
            // Fetch the first entity hit
            final HariantEntity entity = world.getNearbyEntities(location, lookupRadius, lookupRadius, lookupRadius)
                                              .stream()
                                              .map(Hariant::getEntityOrNull)
                                              .filter(_entity -> _entity != null && filter.test(_entity))
                                              .findFirst()
                                              .orElse(null);
            
            // If entity was hit, check for priority
            if (entity != null) {
                // If priority is FIRST_HIT or a player is hit, return them
                if (priority == EntityPriority.FIRST_HIT || entity instanceof HariantPlayer) {
                    return TalentContext.create(entity);
                }
                // Otherwise keep searching for a player
                else if (target == null) {
                    target = entity;
                }
            }
            
            location.subtract(x, y, z);
        }
        
        return target != null ? TalentContext.create(target) : null;
    }
    
    @Override
    public @NotNull Component errorMessage() {
        return Component.text("No valid target!", Colors.RED);
    }
    
    public enum BlockCollision implements Predicate<Block> {
        
        /**
         * Allow the ray cast to go through blocks.
         */
        ALLOW {
            @Override
            public boolean test(@NotNull Block block) {
                return true;
            }
        },
        
        /**
         * Allow the ray cast to go through passable blocks.
         */
        ALLOW_PASSABLE {
            @Override
            public boolean test(@NotNull Block block) {
                return BlockHelper.isPassable(block);
            }
        },
        
        /**
         * Deny ray cast to go through blocks.
         */
        DENY {
            @Override
            public boolean test(@NotNull Block block) {
                return !block.isSolid();
            }
        }
    }
    
    public enum EntityPriority {
        
        /**
         * Gets the first entity hit.
         */
        FIRST_HIT,
        
        /**
         * Prioritize players before other entities.
         */
        PLAYER_PRIORITY
        
    }
    
}
