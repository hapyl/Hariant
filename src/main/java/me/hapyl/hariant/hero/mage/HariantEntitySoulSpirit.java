package me.hapyl.hariant.hero.mage;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.math.Vector3;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.util.decimal.Decimal;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.stream.Stream;

class HariantEntitySoulSpirit extends HariantEntity {
    
    private final HariantPlayer player;
    private final Properties properties;
    private final Vector3 origin;
    
    private HariantEntity target;
    
    HariantEntitySoulSpirit(@NotNull HariantPlayer player, @NotNull Properties properties) {
        super(createEntity(player.getLocationOffsetRandomly(0.75)), Attributes.base(properties.health.doubleValue(), 0, 0));
        
        this.player = player;
        this.properties = properties;
        this.origin = Vector3.ofLocation(this.getLocation());
        
        player.getPlayerTeam().addEntry(this);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        final int tick = getTicksAlive();
        
        if (tick > properties.duration.intValue()) {
            this.explode();
            return;
        }
        
        // Pick the nearest target
        if (target == null || target.isDead()) {
            target = findNearestTarget();
        }
        
        final Location location = this.getLocation();
        
        // If target exists, fly towards it
        if (target != null) {
            // If close enough to the target, explode
            if (distanceToSquared(target) <= 4) {
                this.explode();
                return;
            }
            
            // If we're close to the target
            final Vector vector = target.getMidpointLocation().toVector().subtract(location.toVector()).normalize().multiply(0.1);
            
            location.add(vector);
        }
        // Otherwise just fly at the same place
        else {
            final double y = Math.sin(Math.toRadians(tick * 5)) * 0.5;
            
            location.setY(origin.y() + y);
        }
        
        teleport(location);
        
        // Fx
        spawnWorldParticle(location, Particle.SCULK_SOUL, 1, 0, 0, 0, 0.0f);
        spawnWorldParticle(location, Particle.SOUL, 1, 0, 0, 0, 0.0f);
    }
    
    public void explode() {
        // Deal damage in AoE
        streamEntities(properties.explosionRadius).forEach(entity -> entity.damage(properties.damageSource));
        
        // Fx
        final Location location = getLocation();
        
        spawnWorldParticle(location, Particle.SOUL, 20, 0, 0, 0, 0.1f);
        
        playWorldSound(location, Sound.ENTITY_WARDEN_ROAR, 2.0f);
        playWorldSound(location, Sound.ENTITY_WARDEN_HURT, 0.75f);
        
        this.remove();
    }
    
    @Nullable
    private HariantEntity findNearestTarget() {
        return streamEntities(properties.lookupRadius)
                .min(Comparator.comparingDouble(this::distanceToSquared))
                .orElse(null);
    }
    
    @NotNull
    private Stream<HariantEntity> streamEntities(@NotNull Decimal radius) {
        return collectNearbyEntities(radius).filter(entity -> !this.equals(entity) && player.canAffect(entity));
    }
    
    @NotNull
    private static LivingEntity createEntity(@NotNull Location location) {
        return Entities.SLIME.spawn(location, self -> {
            self.setSize(2);
            self.setInvisible(true);
            self.setSilent(true);
        });
    }
    
    public record Properties(@NotNull Decimal health, @NotNull Decimal lookupRadius, @NotNull Decimal explosionRadius, Decimal duration, @NotNull DamageSource damageSource) {
    }
    
}
