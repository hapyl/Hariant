package me.hapyl.hariant.handler;

import me.hapyl.hariant.entity.damage.DamageSource;
import org.bukkit.entity.Projectile;
import org.jetbrains.annotations.NotNull;

public interface ProjectileConstructor<P extends Projectile, H extends HariantProjectile> {
    
    @NotNull H construct(@NotNull P projectile, @NotNull DamageSource damageSource);
    
}
