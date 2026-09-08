package me.hapyl.hariant.handler;

import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageResult;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.KnockbackSource;
import org.bukkit.block.Block;
import org.bukkit.entity.Projectile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HariantDamageProjectile extends HariantProjectile {
    
    private @NotNull DamageSource damageSource;
    
    public HariantDamageProjectile(@NotNull Projectile projectile, @NotNull HariantEntity shooter, @NotNull DamageSource damageSource) {
        super(projectile, shooter);
        
        this.damageSource = validateDamageSource(damageSource);
    }
    
    public @NotNull DamageSource getDamageSource() {
        return damageSource;
    }
    
    public void setDamageSource(@NotNull DamageSource damageSource) {
        this.damageSource = validateDamageSource(damageSource);
    }
    
    @Override
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
    
    protected @NotNull KnockbackSource createKnockbackSource() {
        return KnockbackSource.create(this, HariantConstants.RANGE_KNOCKBACK_STRENGTH);
    }
    
    private @NotNull DamageSource validateDamageSource(@Nullable DamageSource damageSource) {
        if (damageSource == null) {
            throw new IllegalArgumentException("DamageSource cannot be null!");
        }
        else if (damageSource.getSource() == null) {
            throw new IllegalArgumentException("DamageSource must have a source!");
        }
        else if (!damageSource.getSource().equals(this.getShooter())) {
            throw new IllegalArgumentException("Shooter must not differ!");
        }
        
        return damageSource;
    }
    
}
