package me.hapyl.hariant.hero.orc;

import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.math.Numbers;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.element.ElementSource;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.EntityCollector;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.WarningType;
import me.hapyl.hariant.entity.damage.DamageResult;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.BlockHelper;
import me.hapyl.hariant.util.Models;
import me.hapyl.hariant.util.Origin;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.stream.Stream;

public class WeaponGorkaMorkaEntity extends HariantTickingTask implements EntityCollector {
    
    private static final double PLAYER_COLLISION_SQUARED = Numbers.square(1.25);
    
    private final HariantPlayer player;
    private final WeaponGorkaMorka weapon;
    
    private final Location location;
    private final DisplayEntity display;
    
    private @NotNull Performs performs;
    
    WeaponGorkaMorkaEntity(@NotNull HariantPlayer player, @NotNull WeaponGorkaMorka weapon) {
        super(Scheduler.ofTimer());
        
        this.player = player;
        this.weapon = weapon;
        this.location = player.getLocation().add(0, 1.2, 0);
        this.display = Models.ORC_WEAPON.spawn(location);
        this.performs = new PerformsThrow(location);
    }
    
    public boolean recall() {
        // Don't allow duplicate recalls
        if (this.performs instanceof PerformsRecall) {
            return false;
        }
        
        this.performs.onRecall(this);
        this.performs = new PerformsRecall();
        
        // Fx
        this.player.playWorldSound(location, Sound.BLOCK_BELL_RESONATE, 2.0f);
        
        return true;
    }
    
    @Override
    public void run(int tick) {
        this.performs.tick(this, tick);
    }
    
    @Override
    public void onCancel() {
        this.display.remove();
    }
    
    @Override
    public @NotNull Location getLocation() {
        return location;
    }
    
    public @NotNull Stream<? extends HariantEntity> collectNearbyEntities() {
        return this.collectNearbyEntities(weapon.collisionRadius).filter(player::canAffect);
    }
    
    public interface Performs {
        
        void tick(@NotNull WeaponGorkaMorkaEntity entity, int tick);
        
        void onRecall(@NotNull WeaponGorkaMorkaEntity entity);
        
    }
    
    public static class PerformsThrow implements Performs {
        
        private final Origin origin;
        private final Vector vector;
        private double distanceFlown;
        
        PerformsThrow(@NotNull Location location) {
            this.origin = Origin.create(location);
            this.vector = location.getDirection().normalize();
        }
        
        @Override
        public void tick(@NotNull WeaponGorkaMorkaEntity entity, int tick) {
            // Fly forward
            this.distanceFlown += entity.weapon.throwSpeed.doubleValue();
            
            // If distance flown is higher than the limit, initiate recall
            if (this.distanceFlown > entity.weapon.maximumThrowDistance.doubleValue()) {
                entity.recall();
                return;
            }
            
            final double x = vector.getX() * distanceFlown;
            final double y = vector.getY() * distanceFlown;
            final double z = vector.getZ() * distanceFlown;
            
            origin.merge(entity.location, x, y, z);
            
            // Check for entity collision
            final HariantEntity collision = entity.collectNearbyEntities().findAny().orElse(null);
            
            if (collision != null) {
                // Deal DMG to entity
                final double damage = entity.weapon.calculateDamage(entity.player, distanceFlown);
                final DamageResult damageResult = collision.damage(entity.weapon.createDamageSource(entity.player, damage, damage));
                
                // Damage the DMG was lethal, recall right away
                if (damageResult.isDead()) {
                    entity.recall();
                    return;
                }
                
                // Otherwise perform entity collision
                entity.performs = new PerformsEntityCollision(collision, entity.weapon.createRecallDamageSource(entity.player, damage), entity.location.x() + 1.5 - collision.x());
                return;
            }
            
            // Check for block collision
            final Block block = entity.location.getBlock();
            
            if (!BlockHelper.isPassable(block)) {
                final Block blockAbove = block.getRelative(BlockFace.UP);
                
                // If a block has air above it, anchor on top of the block
                if (!BlockHelper.isSolid(blockAbove)) {
                    entity.location.setY(block.getBoundingBox().getMaxY());
                    entity.display.setRotation(new Vector3f((float) Math.toRadians(15), 0, 0));
                }
                // Otherwise, anchor on the location
                else {
                    // Go back one step so we're not inside a block
                    final double distanceFlownMinusOne = distanceFlown - entity.weapon.throwSpeed.doubleValue() - 0.1;
                    
                    origin.merge(entity.location, vector.getX() * distanceFlownMinusOne, vector.getY() * distanceFlownMinusOne, vector.getZ() * distanceFlownMinusOne);
                    
                    entity.display.resetRotation();
                }
                
                // Synchronize one last time
                entity.performs = new PerformsBlockCollision(entity.location, ElementSource.create(ElementType.ICE, entity.player, entity.weapon.blizzardIceApplication));
                entity.display.teleport(entity.location);
                
                // Play fx of the block collision
                entity.player.playWorldSound(entity.location, block.getBlockSoundGroup().getPlaceSound(), 0.5f);
            }
            
            // Sync display entity
            entity.display.teleport(entity.location);
            
            // Rotate entity
            entity.display.setRotation(new Vector3f((float) Math.sin(Math.PI * 0.5 * Math.toRadians(tick * 5)), 0, 0));
            
            // Fx
            if (tick > 0 && tick % 5 == 0) {
                entity.player.spawnWorldParticle(entity.location, Particle.SWEEP_ATTACK, 1, 0.2, 0.2, 0.2, 0);
            }
        }
        
        @Override
        public void onRecall(@NotNull WeaponGorkaMorkaEntity entity) {
        }
        
    }
    
    public static class PerformsEntityCollision implements Performs {
        
        private final HariantEntity entity;
        private final DamageSource damageSource;
        private final double y;
        
        PerformsEntityCollision(@NotNull HariantEntity entity, @NotNull DamageSource damageSource, final double y) {
            this.entity = entity;
            this.damageSource = damageSource;
            this.y = y;
            
            // Set freeze ticks for entity
            entity.setFreezeTicks(100);
        }
        
        @Override
        public void tick(@NotNull WeaponGorkaMorkaEntity entity, int tick) {
            // If entity has died, start recall
            if (this.entity.isDead()) {
                entity.recall();
                return;
            }
            
            // Sync display entity
            final Location location = this.entity.getLocation();
            
            // Keep axes yaw & pitch
            location.setYaw(entity.location.getYaw());
            location.setPitch(entity.location.getPitch());
            
            // Offset Y so the axe is always at the same height
            location.add(0, y, 0);
            
            entity.display.teleport(location);
        }
        
        @Override
        public void onRecall(@NotNull WeaponGorkaMorkaEntity entity) {
            // Deal additional DMG and knockback on recall
            this.entity.damage(damageSource);
            this.entity.setVelocity(entity.player.getLocation().toVector().subtract(this.entity.getLocation().toVector()).normalize().multiply(entity.weapon.recallStrength.doubleValue()).setY(0.3));
        }
    }
    
    public static class PerformsBlockCollision implements Performs, EntityCollector {
        
        private static final Particle.DustTransition DUST_TRANSITION = new Particle.DustTransition(
                Color.fromRGB(Colors.ELEMENT_ICE.value()),
                Color.fromRGB(11660272),
                1.25f
        );
        
        private final Location location;
        private final ElementSource elementSource;
        
        PerformsBlockCollision(@NotNull Location location, @NotNull ElementSource elementSource) {
            this.location = location;
            this.elementSource = elementSource;
        }
        
        @Override
        public void tick(@NotNull WeaponGorkaMorkaEntity entity, int tick) {
            final Stream<? extends HariantEntity> entities = this.collectNearbyEntities(entity.weapon.blizzardRadius).filter(entity.player::canAffect);
            
            // Affect entities
            if (tick > 0 && tick % entity.weapon.blizzardIceApplicationPeriod.intValue() == 0) {
                entities.forEach(_entity -> _entity.applyElement(elementSource));
                
                // Fx
                entity.player.spawnWorldParticle(location, Particle.SNOWFLAKE, 10, 0.7, 0.3, 0.7, 0.1f);
                
                entity.player.playWorldSound(location, Sound.ENTITY_SNOW_GOLEM_HURT, 0.75f);
                entity.player.playWorldSound(location, Sound.BLOCK_SNOW_BREAK, 0.75f);
            }
            else {
                entities.forEach(_entity -> _entity.showWarning(WarningType.WARNING, 5));
            }
            
            // Fx
            final double radians = Math.toRadians(tick * 5);
            final double blizzardRadius = entity.weapon.blizzardRadius.doubleValue();
            
            final int points = 8;
            final double offset = Math.PI * 2 / points;
            
            for (int i = 0; i < points; i++) {
                final double radiansOffset = radians + offset * i;
                
                final double x = Math.sin(radiansOffset) * blizzardRadius;
                final double y = Math.cos(Math.PI * 0.5 * radians + offset * i) * 0.25;
                final double z = Math.cos(radiansOffset) * blizzardRadius;
                
                spawnParticleOffset(entity.player, location, x, y, z);
            }
            
        }
        
        @Override
        public void onRecall(@NotNull WeaponGorkaMorkaEntity entity) {
            // Explode on recall?
        }
        
        @Override
        public @NotNull Location getLocation() {
            return location;
        }
        
        private void spawnParticleOffset(@NotNull HariantPlayer player, @NotNull Location location, double x, double y, double z) {
            location.add(x, y, z);
            player.spawnWorldParticle(location, Particle.DUST_COLOR_TRANSITION, 1, 0, 0, 0, 0, DUST_TRANSITION);
            location.subtract(x, y, z);
        }
        
    }
    
    public static class PerformsRecall implements Performs {
        
        private int wait;
        
        PerformsRecall() {
        }
        
        @Override
        public void tick(@NotNull WeaponGorkaMorkaEntity entity, int tick) {
            // Wait for a little before recalling
            if (wait++ < entity.weapon.recallWait.intValue()) {
                // Shake the entity a little
                entity.display.setRotation(new Vector3f(
                        entity.player.random.nextFloat() * 0.2f,
                        entity.player.random.nextFloat() * 0.2f,
                        entity.player.random.nextFloat() * 0.2f
                ));
                return;
            }
            
            // Fly towards the player
            final Vector vector = entity.player.getMidpointLocation().toVector().subtract(entity.location.toVector()).normalize().multiply(entity.weapon.recallSpeed.doubleValue());
            
            entity.location.add(vector);
            
            // Convert vector to yaw and pitch
            final float yaw = (float) Math.toDegrees(Math.atan2(-vector.getX(), vector.getZ()));
            
            entity.location.setYaw(yaw + 180);
            
            // Sync entity
            entity.display.teleport(entity.location);
            
            // If close to the player, start the cooldown
            if (entity.player.distanceToSquared(entity.location) < PLAYER_COLLISION_SQUARED) {
                entity.player.getHeroData(HeroRegistry.ORC, HeroDataOrc::new).onWeaponRecall(entity);
            }
        }
        
        @Override
        public void onRecall(@NotNull WeaponGorkaMorkaEntity entity) {
        }
        
    }
    
}