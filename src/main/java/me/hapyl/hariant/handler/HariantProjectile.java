package me.hapyl.hariant.handler;

import me.hapyl.eterna.module.location.Coordinates;
import me.hapyl.eterna.module.location.Distanced;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.hariant.entity.EntityCollector;
import me.hapyl.hariant.entity.HariantEntity;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HariantProjectile implements Coordinates, EntityCollector, Distanced, Removable {
    
    protected final Projectile projectile;
    protected final HariantEntity shooter;
    
    public HariantProjectile(@NotNull Projectile projectile, @NotNull HariantEntity shooter) {
        this.projectile = projectile;
        this.shooter = shooter;
    }
    
    public @NotNull Vector getVelocity() {
        return projectile.getVelocity();
    }
    
    public @NotNull Projectile getProjectile() {
        return projectile;
    }
    
    public @NotNull HariantEntity getShooter() {
        return shooter;
    }
    
    @Override
    public double x() {
        return projectile.getX();
    }
    
    @Override
    public double y() {
        return projectile.getY();
    }
    
    @Override
    public double z() {
        return projectile.getZ();
    }
    
    @Override
    public @NotNull Location getLocation() {
        return new Location(projectile.getWorld(), this.x(), this.y(), this.z());
    }
    
    @Override
    public @NotNull Color outlineColor() {
        return Color.AQUA;
    }
    
    public void onLaunch() {
        this.getShooter().onProjectileLaunched(this);
    }
    
    public void onHit(@Nullable HariantEntity entity, @Nullable Block block) {
    }
    
    @Override
    public void remove() {
        projectile.remove();
    }
    
}