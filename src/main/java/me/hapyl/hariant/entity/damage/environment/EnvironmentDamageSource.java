package me.hapyl.hariant.entity.damage.environment;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.text.Capitalizable;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.cooldown.Cooldown;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DamageSourceImpl;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.damage.component.DamageComponents;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

public class EnvironmentDamageSource extends DamageSourceImpl {
    
    private static final Cooldown COOLDOWN = Cooldown.create(Key.ofString("environment_cooldown"), Tick.ofTicks(10));
    
    EnvironmentDamageSource(@NotNull DamageSourceIdentity identity, @NotNull ElementType elementType, double damage) {
        super(identity, null, DamageType.ENVIRONMENT, elementType, DamageComponents.ofEnvironmentDamage(), Set.of(), damage, 0);
    }
    
    EnvironmentDamageSource(@NotNull org.bukkit.damage.DamageType damageType, @NotNull DeathMessage deathMessage, @NotNull ElementType elementType, double damage) {
        this(createIdentity(damageType, deathMessage), elementType, damage);
    }
    
    @Override
    public final @NotNull Cooldown cooldown() {
        return COOLDOWN;
    }
    
    public boolean isCactus() {
        return this instanceof EnvironmentDamageSourceCactus;
    }
    
    public boolean isCampfire() {
        return this instanceof EnvironmentDamageSourceCampfire;
    }
    
    public boolean isDrown() {
        return this instanceof EnvironmentDamageSourceDrown;
    }
    
    public boolean isExplosion() {
        return this instanceof EnvironmentDamageSourceExplosion;
    }
    
    public boolean isFreeze() {
        return this instanceof EnvironmentDamageSourceFreeze;
    }
    
    public boolean isHotFloor() {
        return this instanceof EnvironmentDamageSourceHotFloor;
    }
    
    public boolean isInFire() {
        return this instanceof EnvironmentDamageSourceInFire;
    }
    
    public boolean isInWall() {
        return this instanceof EnvironmentDamageSourceInWall;
    }
    
    public boolean isLava() {
        return this instanceof EnvironmentDamageSourceLava;
    }
    
    public boolean isOnFire() {
        return this instanceof EnvironmentDamageSourceOnFire;
    }
    
    public boolean isGenericKill() {
        return this instanceof EnvironmentDamageSourceGenericKill;
    }
    
    public boolean isFall() {
        return this instanceof EnvironmentDamageSourceFall;
    }
    
    private static @NotNull DamageSourceIdentity createIdentity(@NotNull org.bukkit.damage.DamageType damageType, @NotNull DeathMessage deathMessage) {
        final String key = damageType.getKey().getKey();
        final Component damageTypeName = Component.text(Capitalizable.capitalize(key.replace("_", " ")));
        
        return DamageSourceIdentity.create(
                Objects.requireNonNull(Key.ofStringOrNull(key), "Invalid damage type key: %s".formatted(key)),
                damageTypeName,
                deathMessage
        );
    }
    
}