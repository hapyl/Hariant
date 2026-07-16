package me.hapyl.hariant.hero.pytaria.bee;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.HariantRandom;
import me.hapyl.hariant.entity.damage.DamageSourceImpl;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.entity.effect.status.EnumStatusEffect;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.pytaria.TalentFeelTheBreeze;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.task.executor.Promise;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public class BeeSwarm extends HariantTickingTask {
    
    private final HariantPlayer player;
    private final TalentFeelTheBreeze talent;
    private final Promise promise;
    
    private final Set<BeePet> bees;
    
    public BeeSwarm(@NotNull HariantPlayer player, @NotNull TalentFeelTheBreeze talent, @NotNull Promise promise) {
        super(Scheduler.ofTimer(1));
        
        this.player = player;
        this.talent = talent;
        this.promise = promise;
        
        this.bees = Sets.newHashSet();
        
        // Create bees
        final Location spawnLocation = LocationHelper.getToTheRight(player.getMidpointLocation(), 1.5);
        
        for (int i = 0; i < talent.numberOfBees.intValue(); i++) {
            this.bees.add(Hariant.createEntity(() -> new BeePet(player, spawnLocation)));
        }
    }
    
    @Override
    public void run(int tick) {
        // Remove dead bees from the swarm
        this.bees.removeIf(BeePet::isDead);
        
        // If all bees have died, fulfill the promise and cancel the task
        if (this.bees.isEmpty()) {
            this.promise.fulfil();
            this.cancel();
            return;
        }
        
        final int size = this.bees.size();
        final double spread = Math.PI * 2 / size;
        
        int beeIndex = 0;
        
        for (final Iterator<BeePet> iterator = this.bees.iterator(); iterator.hasNext(); ) {
            beeIndex++;
            
            final BeePet bee = iterator.next();
            final Location location = bee.getLocation();
            
            // If the bees can't attack yet, float around the player
            if (tick < talent.delayBeforeBeesCanTarget.intValue()) {
                bee.floatAround(player.getMidpointLocation(), spread, beeIndex, tick);
                continue;
            }
            
            // If the bee has a target, check whether they're dead and reset target
            if (bee.target != null) {
                // If target has died or too far away, stop going towards them
                final double distanceToSquared = bee.distanceToSquared(bee.target.getEntity());
                final int chasingFor = bee.target.incrementChasingFor();
                
                if (!player.canAffect(bee.target.getEntity()) || distanceToSquared > talent.maxStrayDistance.doubleValueSquared() || chasingFor > talent.beeGivesUpAfterChasingFor.intValue()) {
                    bee.unsetTarget(talent.beeTargetLossCooldown.intValue());
                }
            }
            // If the bee doesn't have a target, check for the closest entity to the bee
            else {
                // Check for target cooldown
                if (bee.targetCooldown > 0) {
                    bee.targetCooldown--;
                }
                else {
                    final HariantEntity target = player.collectNearbyEntities(location, talent.enemyLookupRadius)
                                                       .filter(player::canAffect)
                                                       .min(Comparator.comparingDouble(bee::distanceToSquared))
                                                       .orElse(null);
                    
                    // If there is a new target, assign it, otherwise go towards pytaria
                    if (target != null) {
                        bee.target = new BeeTarget(target);
                        
                        // Play target fx
                        player.playWorldSound(bee.getLocation(), Sound.ENTITY_BEE_HURT, 0.25f, 1.0f);
                    }
                }
            }
            
            // Randomize the destination
            final Location destination = bee.target != null ? bee.target.getEntity().getMidpointLocation() : player.getMidpointLocation();
            final double distanceToSquared = bee.distanceToSquared(destination);
            
            final HariantRandom random = player.getRandom();
            
            // If the bee is close enough to the target, affect it
            if (distanceToSquared < talent.stingDistance.doubleValueSquared()) {
                // If the target exists, deal damage and FUCKING DIE
                if (bee.target != null) {
                    final double damage = bee.target.getEntity().hasEffect(EnumStatusEffect.ROSE_IVY)
                                          ? talent.beeDamageIvy.getScaledValue(player)
                                          : talent.beeDamage.getScaledValue(player);
                    
                    bee.target.getEntity().damage(new BeeSwarmDamageSource(player, damage));
                    
                    bee.remove();
                    iterator.remove();
                    
                    // Fx
                    player.playWorldSound(location, Sound.ENTITY_BEE_DEATH, 0.5f, 2.0f);
                    player.playWorldSound(location, Sound.ENTITY_BEE_STING, 0.5f, 0.0f);
                    
                    player.spawnWorldParticle(location, Particle.POOF, 5, 0.25, 0.25, 0.25, 0.05f);
                }
                // Otherwise, fly around the player because they smell nice
                else {
                    bee.floatAround(destination, spread, beeIndex, tick);
                }
            }
            // If not close enough, fly towards destination
            else {
                // Randomize the location a little so bees are not inside each other
                destination.add(random.nextSignedDouble(2), random.nextSignedDouble(0.75), random.nextSignedDouble(2));
                
                // Fly towards the destination
                final Vector direction = destination.toVector().subtract(location.toVector()).normalize();
                final double interpolation = Math.min(0.5, Math.sqrt(distanceToSquared) * 0.1);
                
                // If we're close enough
                direction.multiply(interpolation);
                
                // Look towards destination
                final float yaw = (float) Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ()));
                location.setYaw(yaw);
                
                bee.setLocation(location.add(direction));
                bee.setAngry(bee.target != null);
            }
        }
    }
    
    @Override
    public void onCancel() {
        this.bees.forEach(BeePet::remove);
        this.bees.clear();
    }
    
    class BeeSwarmDamageSource extends DamageSourceImpl {
        BeeSwarmDamageSource(@Nullable HariantEntity source, double damage) {
            super(talent.damageSourceIdentity, source, DamageType.ULTIMATE, ElementType.PHYSICAL, DamageComponent.ofCommon(), Set.of(), damage, talent.elementalApplication.doubleValue());
        }
    }
    
}
