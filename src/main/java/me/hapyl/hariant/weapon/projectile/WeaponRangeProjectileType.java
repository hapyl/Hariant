package me.hapyl.hariant.weapon.projectile;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.player.HariantPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public abstract class WeaponRangeProjectileType implements Named, Described {
    
    // TODO (xanyjl @ Saturday, May 30) -> Maybe make this Options or something
    
    private static final double DEFAULT_DISTANCE = 50;
    private static final double DEFAULT_STEP = 0.5;
    private static final double DEFAULT_RADIUS = 1.0;
    
    private static final DamageSourceIdentity DEFAULT_DAMAGE_SOURCE_IDENTITY = DamageSourceIdentity.create(
            Key.ofString("ranged_projectile"),
            Component.text("Ranged Projectile"),
            DeathMessage.create("{player} was shot [by {killer}]")
    );
    
    private final Component name;
    private final Component description;
    
    protected double projectileDistance;
    protected double projectileStep;
    protected double projectileRadius;
    
    @NotNull protected CollisionMode collisionMode;
    @NotNull protected DamageSourceIdentity damageSourceIdentity;
    
    WeaponRangeProjectileType(@NotNull Component name, @NotNull Component description) {
        this.name = name;
        this.description = description;
        this.projectileDistance = DEFAULT_DISTANCE;
        this.projectileStep = DEFAULT_STEP;
        this.projectileRadius = DEFAULT_RADIUS;
        this.collisionMode = CollisionMode.DEFAULT;
        this.damageSourceIdentity = DEFAULT_DAMAGE_SOURCE_IDENTITY;
    }
    
    @Override
    @Deprecated
    public void setName(@NotNull Component name) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    @Deprecated
    public final void setDescription(@NotNull Component description) {
        throw new UnsupportedOperationException();
    }
    
    public double getProjectileDistance() {
        return projectileDistance;
    }
    
    public double getProjectileStep() {
        return projectileStep;
    }
    
    public double getProjectileRadius() {
        return projectileRadius;
    }
    
    @NotNull
    public CollisionMode getCollisionMode() {
        return collisionMode;
    }
    
    @NotNull
    public DamageSourceIdentity getDamageSourceIdentity() {
        return damageSourceIdentity;
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @NotNull
    @Override
    public Component getDescription() {
        return description;
    }
    
    public abstract void onHitEntity(@NotNull HariantPlayer player, @NotNull WeaponRangeProjectile weapon, @NotNull HariantEntity entity);
    
    public abstract void onHitBlock(@NotNull HariantPlayer player, @NotNull WeaponRangeProjectile weapon, @NotNull Block block);
    
    public abstract void onShoot(@NotNull HariantPlayer player, @NotNull WeaponRangeProjectile weapon);
    
    public abstract void onTravel(@NotNull HariantPlayer player, @NotNull WeaponRangeProjectile weapon, @NotNull Location location);
    
    public abstract void launch(@NotNull HariantPlayer player, @NotNull WeaponRangeProjectile projectile);
    
}