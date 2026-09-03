package me.hapyl.hariant.entity;

import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.handler.HariantProjectile;
import me.hapyl.hariant.handler.ProjectileConstructor;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ProjectileLauncher {
    
    <P extends Projectile, H extends HariantProjectile> @NotNull P launchProjectile(@NotNull Class<P> projectileClass, @Nullable Vector velocity, @NotNull DamageSource damageSource, @NotNull ProjectileConstructor<P, H> constructor);
    
    default <P extends Projectile, H extends HariantProjectile> @NotNull P launchProjectile(@NotNull Class<P> projectileClass, @NotNull DamageSource damageSource, @NotNull ProjectileConstructor<P, H> constructor) {
        return this.launchProjectile(projectileClass, null, damageSource, constructor);
    }
    
    default <P extends Projectile> @NotNull P launchProjectile(@NotNull Class<P> projectileClass, @NotNull DamageSource damageSource) {
        return this.launchProjectile(projectileClass, null, damageSource, HariantProjectile::new);
    }
    
}
