package me.hapyl.hariant.hero.shaman;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.EntityCollector;
import me.hapyl.hariant.entity.EntityGarbageCollector;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DamageSourceImpl;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.damage.component.DamageComponents;
import me.hapyl.hariant.entity.heal.HealingSource;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.Models;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Totem implements Ticking, Removable, EntityCollector {
    
    private static final Component TOTEM_RESONANCE = Component.text("Totem Resonance");
    
    private static final float PARTICLE_SCALE = 0.8f;
    private static final float PARTICLE_SPACING = 0.25f;
    
    private static final double PARTICLE_RADIUS_INCREMENT = Math.PI * 0.1;
    
    private static final Particle.DustTransition DUST_TRANSITION_ALLY = new Particle.DustTransition(Color.fromRGB(54, 230, 48), Color.fromRGB(30, 168, 25), PARTICLE_SCALE);
    private static final Particle.DustTransition DUST_TRANSITION_ENEMY = new Particle.DustTransition(Color.fromRGB(212, 65, 49), Color.fromRGB(171, 38, 24), PARTICLE_SCALE);
    
    private final HariantPlayer player;
    private final TalentTotem talent;
    
    private final ArmorStand marker;
    private final DisplayEntity[] totems;
    
    private int tick;
    private boolean hasLanded;
    
    public Totem(@NotNull HariantPlayer player, @NotNull TalentTotem talent) {
        this.player = player;
        this.talent = talent;
        this.marker = createMarker(player);
        this.totems = createTotems(player);
        
        // Show totems based on the team
        Hariant.getPlayers().forEach(other -> {
            final DisplayEntity totemToHide = player.isSelfOrTeammate(other) ? totems[1] : totems[0];
            final Player otherPlayer = other.getHandle();
            
            // Hide all children of the totem
            totemToHide.forEach(child -> otherPlayer.hideEntity(Hariant.getPlugin(), child.getDisplay()));
        });
        
        // TODO (xanyjl @ Thursday, November 12) -> Glow the totems
        
        // Throw the totem
        this.marker.setVelocity(player.getDirection().setY(0).normalize().multiply(talent.tossVelocity.doubleValue()).setY(talent.tossY.doubleValue()));
    }
    
    @Override
    public void remove() {
        marker.remove();
        
        for (DisplayEntity totem : totems) {
            totem.remove();
        }
        
        // If expired, play sfx
        if (tick > talent.duration()) {
            final Location location = marker.getLocation();
            
            player.playWorldSound(location, Sound.ENTITY_IRON_GOLEM_DAMAGE, 0.25f);
            player.spawnWorldParticle(location, Particle.CRIT, 25, 0.2, 0.5, 0.2, 0.5f);
        }
    }
    
    @Override
    public boolean shouldRemove() {
        return marker.getY() < 0 || tick > talent.duration();
    }
    
    @Override
    public void tick() {
        tick++;
        
        // Sync totems to the marker
        final Location location = marker.getLocation();
        location.setPitch(0.0f);
        
        for (DisplayEntity totem : totems) {
            totem.teleport(location);
        }
        
        // Check for landing
        if (!hasLanded && marker.isOnGround()) {
            hasLanded = true;
            
            // Deal damage upon landing
            final DamageSourceTotemLanding damageSource = new DamageSourceTotemLanding(player, talent.totemLandingDamage.getScaledValue(player));
            
            stream().filter(player::canAffect).forEach(entity -> entity.damage(damageSource));
            
            // Fx
            player.playWorldSound(getLocation(), Sound.ENTITY_IRON_GOLEM_REPAIR, 1.0f);
        }
        
        // Resonate
        if (tick % talent.resonancePeriod.intValue() == 0) {
            final HealingSource healingSource = HealingSource.create(talent.totemResonanceHealing.getScaledValue(player), TOTEM_RESONANCE, player);
            final DamageSourceTotemResonance damageSource = new DamageSourceTotemResonance(player, talent.totemResonanceDamage.getScaledValue(player), talent.totemResonanceElementalApplication.doubleValue());
            
            // Partition entities in range by ally/enemy
            this.stream().forEach(entity -> {
                if (entity.isSelfOrTeammate(player)) {
                    entity.heal(healingSource);
                }
                else {
                    entity.damage(damageSource);
                }
            });
            
            // We play the fx once to reduce tasks to one
            playTotemFx();
        }
    }
    
    @Override
    public @NotNull Location getLocation() {
        return offsetLocation(marker.getLocation());
    }
    
    private void playTotemFx() {
        new HariantTickingTask(Scheduler.ofTimer()) {
            private double radius = 0.2;
            
            @Override
            public void run(int tick) {
                if (radius > talent.resonanceRadius.doubleValue() + PARTICLE_RADIUS_INCREMENT) {
                    this.cancel();
                    return;
                }
                
                // We get the marker's location because `getLocation` offsets it to the centre
                final Location location = marker.getLocation();
                
                // We calculate points once a tick then draw for each entity
                final List<Location> points = Lists.newArrayList();
                
                for (double delta = -radius; delta < radius; delta += PARTICLE_SPACING) {
                    points.add(LocationHelper.copyOf(location).add(delta, 0, -radius));
                    points.add(LocationHelper.copyOf(location).add(radius, 0, delta));
                    points.add(LocationHelper.copyOf(location).add(-delta, 0, radius));
                    points.add(LocationHelper.copyOf(location).add(-radius, 0, -delta));
                }
                
                // Draw the points
                for (Location point : points) {
                    Hariant.getPlayers().forEach(entity -> {
                        entity.spawnParticle(point, Particle.DUST_COLOR_TRANSITION, 1, 0, 0, 0, 0.0f, entity.isSelfOrTeammate(player) ? DUST_TRANSITION_ALLY : DUST_TRANSITION_ENEMY);
                    });
                }
                
                // Increment radius
                radius += PARTICLE_RADIUS_INCREMENT;
            }
        };
    }
    
    private @NotNull Stream<? extends HariantEntity> stream() {
        return this.collectNearbyEntities(talent.resonanceRadius);
    }
    
    @Contract(mutates = "param1")
    private static @NotNull Location offsetLocation(@NotNull Location location) {
        location.add(0, 1, 0);
        location.setPitch(0f);
        
        return location;
    }
    
    private static @NotNull ArmorStand createMarker(@NotNull HariantPlayer player) {
        return player.getWorld().spawn(
                offsetLocation(player.getLocation()),
                ArmorStand.class,
                self -> {
                    self.setSmall(true);
                    self.setVisible(false);
                    self.setInvulnerable(true);
                    
                    EntityGarbageCollector.add(self);
                }
        );
    }
    
    private static DisplayEntity @NotNull [] createTotems(@NotNull HariantPlayer player) {
        final Location location = offsetLocation(player.getLocation());
        
        return new DisplayEntity[] { Models.TOTEM_ALLY.spawn(location), Models.TOTEM_ENEMY.spawn(location) };
    }
    
    public static class DamageSourceTotemLanding extends DamageSourceImpl {
        
        private static final DamageSourceIdentity DAMAGE_SOURCE_IDENTITY = DamageSourceIdentity.create(
                Key.ofString("totem_landing"),
                Component.text("Totem (Landing)"),
                DeathMessage.create("{player} had [{killer}'s] Totem land on them")
        );
        
        DamageSourceTotemLanding(@NotNull HariantEntity source, double damage) {
            super(DAMAGE_SOURCE_IDENTITY, source, DamageType.TALENT, ElementType.PHYSICAL, DamageComponents.ofCommon(), Set.of(), damage, 0);
        }
        
    }
    
    public static class DamageSourceTotemResonance extends DamageSourceImpl {
        
        private static final DamageSourceIdentity DAMAGE_SOURCE_IDENTITY = DamageSourceIdentity.create(
                Key.ofString("totem_resonance"),
                Component.text("Totem"),
                DeathMessage.create("{player} had killed by [{killer}'s] Totem")
        );
        
        DamageSourceTotemResonance(@NotNull HariantEntity source, double damage, double elementalUnits) {
            super(DAMAGE_SOURCE_IDENTITY, source, DamageType.TALENT, ElementType.TOXIC, DamageComponents.ofCommon(), Set.of(), damage, elementalUnits);
        }
        
    }
    
}