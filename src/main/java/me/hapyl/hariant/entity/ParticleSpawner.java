package me.hapyl.hariant.entity;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ParticleSpawner {
    
    // *-* Local Particle *-* //
    
    <T> void spawnParticle(@NotNull Location location, @NotNull Particle particle, final int amount, final double x, final double y, final double z, final float speed, @Nullable T data);
    
    default void spawnParticle(@NotNull Location location, @NotNull Particle particle, final int amount, final double x, final double y, final double z, final float speed) {
        this.spawnParticle(location, particle, amount, x, y, z, speed, null);
    }
    
    default void spawnParticle(@NotNull Location location, @NotNull Particle particle, final int amount, final float speed) {
        this.spawnParticle(location, particle, amount, 0.0, 0.0, 0.0, speed, null);
    }
    
    // *-* World Particle *-* //
    
    <T> void spawnWorldParticle(@NotNull Location location, @NotNull Particle particle, final int amount, final double x, final double y, final double z, final float speed, @Nullable T data);
    
    default void spawnWorldParticle(@NotNull Location location, @NotNull Particle particle, final int amount, final double x, final double y, final double z, final float speed) {
        this.spawnWorldParticle(location, particle, amount, x, y, z, speed, null);
    }
    
    default void spawnWorldParticle(@NotNull Location location, @NotNull Particle particle, final int amount, final float speed) {
        this.spawnWorldParticle(location, particle, amount, 0.0, 0.0, 0.0, speed, null);
    }
    
    default <T> void spawnWorldParticle(@NotNull Location location, @NotNull Particle particle, final int amount, final float speed, @Nullable T data) {
        this.spawnWorldParticle(location, particle, amount, 0.0, 0.0, 0.0, speed, data);
    }
    
    
}
