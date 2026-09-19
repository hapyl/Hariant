package me.hapyl.hariant.hero.mage;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.inventory.builder.ItemBuilder;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.hariant.achievement.AchievementMageSoulStorm;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.WarningType;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponents;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class SoulStorm extends HariantTickingTask {
    
    public static final ItemStack SOUL_TEXTURE = ItemBuilder.playerHead("fd4c142c382a6be00caeee9a02306f06544e6e46b953bcc8cac65e5c89d4790a").asIcon();
    
    private static final DamageSourceIdentity DAMAGE_SOURCE_IDENTITY = DamageSourceIdentity.create(
            Key.ofString("soul_storm_damage"),
            Component.text("Soul Storm"),
            DeathMessage.create("{player}'s soul was stormed to death [by {killer}]")
    );
    
    private final HariantPlayer player;
    private final TalentSoulStorm talent;
    private final Location location;
    private final Vector vector;
    private final DamageSource damageSource;
    private final Set<Soul> souls;
    private final Map<HariantEntity, Integer> numberOfTimesHit;
    
    private double closestSoul;
    private double furthestSoul;
    
    private final int damagePeriod;
    private final int damagePeriodHalf;
    
    SoulStorm(@NotNull HariantPlayer player, @NotNull Location location, @NotNull Vector vector, @NotNull TalentSoulStorm talent) {
        super(Scheduler.ofTimer());
        
        this.player = player;
        this.talent = talent;
        this.location = location;
        this.vector = vector;
        this.damageSource = new SoulStormDamageSource(player, talent);
        this.souls = Sets.newHashSet();
        this.damagePeriod = talent.damagePeriod.intValue();
        this.damagePeriodHalf = damagePeriod / 2;
        this.numberOfTimesHit = Maps.newHashMap();
    }
    
    @Override
    public void run(int tick) {
        // Always tick souls
        final Iterator<Soul> iterator = souls.iterator();
        
        while (iterator.hasNext()) {
            final Soul soul = iterator.next();
            
            if (soul.tick()) {
                soul.remove();
                iterator.remove();
            }
        }
        
        // The vector never changes, so we only need to know the closest and furthest soul to check for collisions
        if (tick % damagePeriodHalf == 0 && furthestSoul > 1) {
            final boolean damageTick = tick % damagePeriod == 0;
            
            final Location location = LocationHelper.copyOf(this.location);
            
            for (double d = closestSoul; d < furthestSoul; d += talent.radius.doubleValueSquared()) {
                location.set(
                        this.location.getX() + vector.getX() * d,
                        this.location.getY() + vector.getY() * d,
                        this.location.getZ() + vector.getZ() * d
                );
                
                final Stream<? extends HariantEntity> entities = player.collectNearbyEntities(location, talent.radius).filter(player::canAffect);
                
                entities.forEach(entity -> {
                    if (damageTick && entity.damage(damageSource) == DamageResult.OK) {
                        // Fx
                        entity.playWorldSound(Sound.BLOCK_SOUL_SAND_BREAK, 0.0f);
                        entity.playWorldSound(Sound.BLOCK_SOUL_SAND_STEP, 0.0f);
                        
                        // Achievement
                        AchievementMageSoulStorm.progress(player, numberOfTimesHit.merge(entity, 1, Integer::sum));
                    }
                    
                    // Warning
                    entity.showWarning(WarningType.DANGER, 5);
                });
            }
            
            // Reset closest soul
            closestSoul = talent.distance.doubleValue();
        }
        
        // If duration is done, stop spawning souls, but only cancel if all souls are dead
        if (tick >= talent.getDuration()) {
            if (souls.isEmpty()) {
                this.cancel();
            }
            
            return;
        }
        
        // Spawn bullet
        if (tick % 2 == 0) {
            souls.add(new Soul(player, this));
            
            // Fx
            player.playWorldSound(location, Sound.BLOCK_SOUL_SAND_BREAK, 0.75f + (1.25f - 0.75f) * player.random.nextFloat());
        }
    }
    
    @Override
    public void onCancel() {
        souls.forEach(Soul::remove);
        souls.clear();
    }
    
    private static class SoulStormDamageSource extends DamageSourceImpl {
        
        SoulStormDamageSource(@NotNull HariantEntity source, @NotNull TalentSoulStorm talent) {
            super(DAMAGE_SOURCE_IDENTITY, source, DamageType.ULTIMATE, ElementType.AETHER, DamageComponents.ofCommon(), Set.of(), talent.damage.getScaledValue(source), talent.elementalApplication.doubleValue(), talent.cooldownKey, talent.damagePeriod.intValue());
        }
        
    }
    
    private static class Soul implements Removable {
        
        private static final double ARMOR_STAND_HEIGHT = 1.975;
        
        private final HariantPlayer player;
        private final Entity entity;
        private final SoulStorm soulStorm;
        
        private final double[] randomOffset;
        
        private double distanceFlown;
        
        Soul(@NotNull HariantPlayer player, @NotNull SoulStorm soulStorm) {
            this.player = player;
            this.randomOffset = new double[] { soulStorm.generateRandomSoulOffset(), soulStorm.generateRandomSoulOffset() - ARMOR_STAND_HEIGHT, soulStorm.generateRandomSoulOffset() };
            this.entity = createEntity(LocationHelper.copyOf(soulStorm.location).add(randomOffset[0], randomOffset[1], randomOffset[2]));
            this.soulStorm = soulStorm;
        }
        
        @Override
        public void remove() {
            entity.remove();
        }
        
        public boolean tick() {
            this.distanceFlown += 1.5;
            
            // Update closest and furthest distances
            soulStorm.closestSoul = Math.min(soulStorm.closestSoul, distanceFlown);
            soulStorm.furthestSoul = Math.max(soulStorm.furthestSoul, distanceFlown);
            
            final double x = soulStorm.location.x() + soulStorm.vector.getX() * distanceFlown;
            final double y = soulStorm.location.y() + soulStorm.vector.getY() * distanceFlown;
            final double z = soulStorm.location.z() + soulStorm.vector.getZ() * distanceFlown;
            
            final Location location = new Location(entity.getWorld(), x + randomOffset[0], y + randomOffset[1], z + randomOffset[2], entity.getYaw(), entity.getPitch());
            
            // Teleport entity to the location
            entity.teleport(location);
            
            // Offset the location based on armor stand height
            location.add(0, ARMOR_STAND_HEIGHT, 0);
            
            player.spawnWorldParticle(location, Particle.SOUL, 1, 0.1, 0.1, 0.1, 0.075f);
            player.spawnWorldParticle(location, Particle.SCULK_SOUL, 1, 0.1, 0.1, 0.1, 0.075f);
            
            return distanceFlown >= soulStorm.talent.distance.doubleValue();
        }
        
        private static @NotNull Entity createEntity(@NotNull Location location) {
            return location.getWorld().spawn(location, ArmorStand.class, self -> {
                self.setVisible(false);
                self.setMarker(true);
                self.getEquipment().setHelmet(SOUL_TEXTURE);
            });
        }
        
    }
    
    private double generateRandomSoulOffset() {
        return player.random.nextSignedDouble(talent.radius.doubleValue());
    }
    
}
