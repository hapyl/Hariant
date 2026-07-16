package me.hapyl.hariant.entity;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.hariant.entity.damage.AssistSource;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import net.kyori.adventure.text.Component;
import org.bukkit.Input;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PullSource extends HariantTickingTask implements EntityCollector {
    
    private static final double VERY_CLOSE = 0.05;
    private static final double BLOCKS_PER_SECOND = 2.15;
    
    private final HariantEntity source;
    private final Location centre;
    private final AssistSource assistSource;
    
    private final int duration;
    private final double radius;
    private final double strength;
    private final double resistance;
    
    public PullSource(@NotNull HariantEntity source, @NotNull Location centre, @NotNull Component name, int duration, double radius, double strength, double resistance) {
        super(Scheduler.ofTimer());
        
        this.source = source;
        this.centre = centre;
        this.assistSource = AssistSource.create(source, name);
        this.duration = duration;
        this.radius = radius;
        this.strength = strength;
        this.resistance = Math.clamp(resistance, 0.0, 1.0);
    }
    
    @Override
    public @NotNull Location getLocation() {
        return centre;
    }
    
    @Override
    public void run(int tick) {
        if (tick > duration) {
            this.cancel();
            return;
        }
        
        this.collectNearbyEntities(radius)
            .filter(source::canAffect)
            .filter(entity -> !entity.hasEffectResistance(assistSource))
            .forEach(this::pull);
    }
    
    @EventLike
    public void onPull(@NotNull HariantEntity entity) {
    }
    
    private void pull(@NotNull HariantEntity entity) {
        final Location location = entity.getLocation();
        final Vector vector = centre.toVector().subtract(location.toVector()).setY(0);
        
        final double length = vector.length();
        
        // Already at the center, do not apply pull forces to prevent jittering
        if (length < VERY_CLOSE) {
            entity.setVelocity(new Vector(0, 0, 0));
            return;
        }
        
        final Vector pullDirection = vector.clone().normalize();
        
        final double pullForce = length < strength ? Math.max(0, length) : strength;
        final Vector pullVector = pullDirection.multiply(pullForce);
        
        // Calculate resistance
        final Vector inputVector = getInputVector(entity);
        final double movementSpeed = getMovementSpeed(entity);
        
        final Vector finalVector;
        
        if (inputVector.lengthSquared() > 0) {
            final Vector actualPull = pullVector.clone().multiply(1 - resistance);
            final Vector playerMove = inputVector.multiply(resistance * movementSpeed);
            
            finalVector = actualPull.add(playerMove);
        }
        else {
            finalVector = pullVector;
        }
        
        // Preserve standard falling/jumping Y velocity
        finalVector.setY(entity.getVelocity().getY() * 0.8);
        
        entity.setVelocity(finalVector);
        
        // Call event-like method
        this.onPull(entity);
    }
    
    private static @NotNull Vector getInputVector(@NotNull HariantEntity entity) {
        final Input input = entity.getCurrentInput();
        final Vector inputVector = new Vector(0, 0, 0);
        
        final double radians = Math.toRadians(entity.getLocation().getYaw());
        final double fx = -Math.sin(radians);
        final double fz = Math.cos(radians);
        
        final Vector forward = new Vector(fx, 0, fz);
        final Vector left = new Vector(fz, 0, -fx);
        
        if (input.isForward()) {
            inputVector.add(forward);
        }
        if (input.isBackward()) {
            inputVector.subtract(forward);
        }
        if (input.isLeft()) {
            inputVector.add(left);
        }
        if (input.isRight()) {
            inputVector.subtract(left);
        }
        
        return inputVector.lengthSquared() > 0 ? inputVector.normalize() : inputVector;
    }
    
    private static double getMovementSpeed(@NotNull HariantEntity entity) {
        return Objects.requireNonNull(entity.getHandle().getAttribute(Attribute.MOVEMENT_SPEED)).getValue() * BLOCKS_PER_SECOND;
    }
    
}