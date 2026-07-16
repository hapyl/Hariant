package me.hapyl.hariant.entity.damage.environment;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.text.Capitalizable;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class EnvironmentDamageSource implements DamageSource {
    
    private static final Key COOLDOWN_KEY = Key.ofString("environment_cooldown");
    
    private final DamageSourceIdentity identity;
    private final ElementType elementType;
    private final double damage;
    
    EnvironmentDamageSource(@NotNull DamageSourceIdentity identity, @NotNull ElementType elementType, double damage) {
        this.identity = identity;
        this.elementType = elementType;
        this.damage = damage;
    }
    
    EnvironmentDamageSource(@NotNull org.bukkit.damage.DamageType damageType, @NotNull DeathMessage deathMessage, @NotNull ElementType elementType, double damage) {
        this(createIdentity(damageType, deathMessage), elementType, damage);
    }
    
    @NotNull
    @Override
    public DamageSourceIdentity getIdentity() {
        return identity;
    }
    
    @Nullable
    @Override
    public HariantEntity getSource() {
        return null;
    }
    
    @NotNull
    @Override
    public ElementType getElementType() {
        return elementType;
    }
    
    @NotNull
    @Override
    public Key getCooldownKey() {
        return COOLDOWN_KEY;
    }
    
    @Override
    public int getCooldown() {
        return 10;
    }
    
    @NotNull
    @Override
    public DamageType getDamageType() {
        return DamageType.ENVIRONMENT;
    }
    
    @NotNull
    @Override
    public List<? extends DamageComponent> getDamageComponents() {
        return DamageComponent.environmentDamage();
    }
    
    @Override
    public @NotNull Set<? extends DamageFlag> getDamageFlags() {
        return Set.of();
    }
    
    @Override
    public double getDamage() {
        return damage;
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
    
    @NotNull
    private static DamageSourceIdentity createIdentity(@NotNull org.bukkit.damage.DamageType damageType, @NotNull DeathMessage deathMessage) {
        final String key = damageType.getKey().getKey();
        final Component damageTypeName = Component.text(Capitalizable.capitalize(key.replace("_", " ")));
        
        return DamageSourceIdentity.create(
                Objects.requireNonNull(Key.ofStringOrNull(key), "Invalid damage type key: %s".formatted(key)),
                damageTypeName,
                deathMessage
        );
    }
    
}