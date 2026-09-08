package me.hapyl.hariant.entity;

import me.hapyl.hariant.handler.HariantProjectile;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ProjectileLauncher {
    
    <P extends Projectile, H extends HariantProjectile> @NotNull H launchProjectile(@NotNull Class<P> projectileClass, @Nullable Vector velocity, @NotNull ProjectileLauncher.ProjectileCreator<P, H> creator);
    
    default <P extends Projectile> @NotNull HariantProjectile launchProjectile(@NotNull Class<P> projectileClass, @Nullable Vector velocity) {
        return launchProjectile(projectileClass, velocity, HariantProjectile::new);
    }
    
    interface ProjectileCreator<P extends Projectile, H extends HariantProjectile> {
        
        @NotNull H create(@NotNull P projectile, @NotNull HariantEntity entity);
        
    }
    
}
