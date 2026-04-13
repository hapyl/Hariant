package me.hapyl.hariant.handler;

import me.hapyl.eterna.module.location.Coordinates;
import me.hapyl.eterna.module.location.Distanced;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.util.Handle;
import me.hapyl.hariant.entity.EntityCollector;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageSource;
import org.bukkit.Location;
import org.bukkit.entity.Projectile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class HariantProjectile implements Handle<Projectile>, Coordinates, EntityCollector, Distanced {
    
    private final Projectile projectile;
    private DamageSource damageSource;
    
    HariantProjectile(@NotNull Projectile projectile, @NotNull DamageSource damageSource) {
        this.projectile = projectile;
        this.damageSource = validateDamageSource(damageSource);
    }
    
    @NotNull
    @Override
    public Projectile getHandle() {
        return projectile;
    }
    
    @NotNull
    public HariantEntity getShooter() {
        return Objects.requireNonNull(damageSource.getSource(), "DamageSource missing shooter somehow!");
    }
    
    @NotNull
    public DamageSource getDamageSource() {
        return damageSource;
    }
    
    public void setDamageSource(@NotNull DamageSource damageSource) {
        this.damageSource = validateDamageSource(damageSource);
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
    
    @NotNull
    @Override
    public Location getLocation() {
        return LocationHelper.defaultLocation(this.x(), this.y(), this.z());
    }
    
    @NotNull
    private static DamageSource validateDamageSource(@Nullable DamageSource damageSource) {
        if (damageSource == null) {
            throw new IllegalArgumentException("DamageSource cannot be null!");
        }
        else if (damageSource.getSource() == null) {
            throw new IllegalArgumentException("DamageSource must have a source!");
        }
        
        return damageSource;
    }
    
}
