package me.hapyl.hariant.entity.ferocity;

import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.math.geometry.Geometry;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public class Ferocity extends HariantTickingTask {
    
    private static final int FEROCITY_DELAY = 9;
    private static final int FEROCITY_PERIOD = 3;
    
    private static final Scheduler SCHEDULER = Scheduler.ofTimer(FEROCITY_DELAY, FEROCITY_PERIOD);
    private static final Particle.DustTransition DUST_TRANSITION = new Particle.DustTransition(Color.fromRGB(77, 2, 8), Color.fromRGB(181, 43, 54), 0.8f);
    
    private final HariantEntity entity;
    private final FerocitySource ferocitySource;
    private final int ferocityStrikes;
    
    // Set damage type to FEROCITY and zero elemental units
    
    public Ferocity(@NotNull HariantEntity entity, @NotNull FerocitySource ferocitySource) {
        super(SCHEDULER);
        
        this.entity = entity;
        this.ferocitySource = ferocitySource;
        this.ferocityStrikes = ferocitySource.ferocityStrikes();
    }
    
    @Override
    public void run(int tick) {
        // If entity is no longer valid for ferocity or exceeding the strike limit, cancel
        if (entity.isDead() || tick >= ferocityStrikes) {
            this.cancel();
            return;
        }
        
        // Damage the entity; FerocitySource#getDamageInstance() gets a prepared copy of the original damage instance
        entity.damage0(ferocitySource.getDamageInstance());
        
        // Fx
        this.spawnFerocityFx();
    }
    
    private void spawnFerocityFx() {
        final Location location = entity.getLocation();
        
        entity.playWorldSound(location, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.5f, 1.75f);
        entity.playWorldSound(location, Sound.ENTITY_DONKEY_HURT, 0.5f, 1.25f);
        
        final double eyeHeight = entity.getEyeHeight();
        
        final double x = entity.random.nextSignedDouble(1.25);
        final double z = entity.random.nextSignedDouble(1.25);
        
        Geometry.drawLine(
                LocationHelper.copyOfPosition(location).add(x, eyeHeight, z),
                LocationHelper.copyOfPosition(location).subtract(x, 0, z), 0.2d,
                _location -> entity.spawnWorldParticle(_location, Particle.DUST_COLOR_TRANSITION, 1, 0, 0, 0, 0, DUST_TRANSITION)
        );
    }
    
}
