package me.hapyl.hariant.hero.nyx;

import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponents;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.Models;
import me.hapyl.hariant.util.QuaternionRotation;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public final class WiltedRose extends HariantTickingTask {
    
    private static final Map<String, QuaternionRotation> PETAL_ROTATION_MAP = Map.of(
            "north", (quaternion, angle) -> quaternion.rotateLocalX(-angle),
            "south", (quaternion, angle) -> quaternion.rotateLocalX(+angle),
            "west", (quaternion, angle) -> quaternion.rotateLocalZ(+angle),
            "east", (quaternion, angle) -> quaternion.rotateLocalZ(-angle)
    );
    
    private static final DamageSourceIdentity DAMAGE_SOURCE_IDENTITY = DamageSourceIdentity.create(
            Key.ofString("wilted_rose"),
            Component.text("Wilted Rose"),
            DeathMessage.createWithDefaultKiller("{player} has wilted to death")
    );
    
    private static final float PETAL_ROTATION_RADIANS = (float) Math.toRadians(30);
    private static final Particle.DustOptions DUST_OPTIONS = new Particle.DustOptions(Color.fromRGB(126, 65, 80), 1);
    
    private final HariantPlayer player;
    private final Location location;
    private final DisplayEntity displayEntity;
    private final TalentReverberation talent;
    
    private final int bloomDelay;
    private final float rotationAngle;
    
    public WiltedRose(@NotNull HariantPlayer player, @NotNull Location location, @NotNull TalentReverberation talent) {
        super(Scheduler.ofTimer());
        
        this.player = player;
        this.location = location;
        this.displayEntity = Models.WILTED_ROSE.spawn(location);
        this.talent = talent;
        this.bloomDelay = talent.roseBloomDelay.intValue();
        this.rotationAngle = PETAL_ROTATION_RADIANS / bloomDelay;
    }
    
    @Override
    public void run(int tick) {
        if (tick > bloomDelay) {
            this.cancel();
            this.bloom();
            return;
        }
        
        // Fx
        final Location location = displayEntity.getLocation();
        final double progress = (double) tick / bloomDelay;
        
        location.add(0, Math.sin(Math.PI * progress) * 0.125, 0);
        location.setYaw(location.getYaw() + 10);
        
        displayEntity.teleport(location);
        
        // Animate petals
        PETAL_ROTATION_MAP.forEach((tag, rotation) -> {
            displayEntity.stream(tag).forEach(display -> display.setRotation(rotation.rotate(display.getRotation(), rotationAngle)));
        });
        
        // Particle Fx to mask the ugly animation
        player.spawnWorldParticle(location, Particle.DUST, 5, 0.2, 0.2, 0.2, 0.1f, DUST_OPTIONS);
        player.playWorldSound(location, Sound.BLOCK_BIG_DRIPLEAF_BREAK, 0.5f + (float) tick / bloomDelay);
    }
    
    public void bloom() {
        final DamageSource damageSource = new WilterRoseDamageSource(player, talent.roseDamage.getScaledValue(player), talent.roseElementalApplication.doubleValue());
        
        player.collectNearbyEntities(location.add(0, 1, 0), talent.roseExplosionRadius)
              .filter(player::canAffect)
              .forEach(entity -> {
                  entity.damage(damageSource);
              });
        
        // Fx
        player.playWorldSound(location, Sound.ENTITY_WITHER_HURT, 1.5f);
        player.spawnWorldParticle(location, Particle.EXPLOSION_EMITTER, 1, 0);
    }
    
    @Override
    public void onCancel() {
        displayEntity.remove();
    }
    
    public static class WilterRoseDamageSource extends DamageSourceImpl {
        WilterRoseDamageSource(@Nullable HariantEntity source, double damage, double elementalApplication) {
            super(DAMAGE_SOURCE_IDENTITY, source, DamageType.TALENT, ElementType.AETHER, DamageComponents.ofCommon(), Set.of(), damage, elementalApplication);
        }
    }
    
}