package me.hapyl.hariant.handler;

import me.hapyl.eterna.module.location.Coordinates;
import me.hapyl.eterna.module.location.Distanced;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.entity.EntityCollector;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageResult;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.KnockbackSource;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Projectile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Objects;

public class HariantProjectile implements Coordinates, EntityCollector, Distanced, Removable {
    
    private final Projectile projectile;
    private @NotNull DamageSource damageSource;
    
    public HariantProjectile(@NotNull Projectile projectile, @NotNull DamageSource damageSource) {
        this.projectile = projectile;
        this.damageSource = validateDamageSource(damageSource);
    }
    
    public @NotNull Projectile getProjectile() {
        return projectile;
    }
    
    public @NotNull HariantEntity getShooter() {
        return Objects.requireNonNull(damageSource.getSource(), "DamageSource missing shooter somehow!");
    }
    
    public @NotNull DamageSource getDamageSource() {
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
    
    @Override
    public @NotNull Location getLocation() {
        return LocationHelper.defaultLocation(this.x(), this.y(), this.z());
    }
    
    @Override
    public @NotNull Color outlineColor() {
        return Color.AQUA;
    }
    
    @OverridingMethodsMustInvokeSuper
    public void onLaunch() {
        this.getShooter().onProjectileLaunched(this);
    }
    
    public void onHit(@Nullable HariantEntity entity, @Nullable Block block) {
        final HariantEntity shooter = this.getShooter();
        
        // If hit entity, handle damage
        if (entity != null) {
            if (entity.damage(damageSource) == DamageResult.OK) {
                entity.knockback(this.createKnockbackSource());
            }
            
            // Sfx
            ProjectileHandler.playHitSound(shooter);
        }
    }
    
    @Override
    public void remove() {
        projectile.remove();
    }
    
    protected @NotNull KnockbackSource createKnockbackSource() {
        return KnockbackSource.create(this, HariantConstants.RANGE_KNOCKBACK_STRENGTH);
    }
    
    private static @NotNull DamageSource validateDamageSource(@Nullable DamageSource damageSource) {
        if (damageSource == null) {
            throw new IllegalArgumentException("DamageSource cannot be null!");
        }
        else if (damageSource.getSource() == null) {
            throw new IllegalArgumentException("DamageSource must have a source!");
        }
        
        return damageSource;
    }
    
}
