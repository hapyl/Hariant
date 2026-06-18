package me.hapyl.hariant.entity.damage;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.cooldown.Cooldown;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Set;

public class DamageSourceImpl implements DamageSource {
    
    @Nullable private final HariantEntity source;
    
    @NotNull private final DamageSourceIdentity identity;
    @NotNull private final ElementType elementType;
    @NotNull private final DamageType damageType;
    
    @NotNull @Unmodifiable private final List<? extends DamageComponent> damageComponents;
    @NotNull @Unmodifiable private final Set<? extends DamageFlag> damageFlags;
    
    private final double damage;
    private final double elementUnits;
    
    private final Key cooldownKey;
    private final int cooldown;
    
    // #norender
    public DamageSourceImpl(@NotNull DamageSourceIdentity identity, @Nullable HariantEntity source, @NotNull DamageType damageType, @NotNull ElementType elementType, @NotNull @Unmodifiable List<? extends DamageComponent> damageComponents, @NotNull @Unmodifiable Set<? extends DamageFlag> damageFlags, final double damage, final double elementUnits, @NotNull Key cooldownKey, int cooldown) {
        this.identity = identity;
        this.source = source;
        this.elementType = elementType;
        this.damageType = damageType;
        this.damageComponents = damageComponents;
        this.damageFlags = damageFlags;
        this.damage = damage;
        this.elementUnits = elementUnits;
        this.cooldownKey = cooldownKey;
        this.cooldown = cooldown;
    }
    
    public DamageSourceImpl(@NotNull DamageSourceIdentity identity, @Nullable HariantEntity source, @NotNull DamageType damageType, @NotNull ElementType elementType, @NotNull @Unmodifiable List<? extends DamageComponent> damageComponents, @NotNull @Unmodifiable Set<? extends DamageFlag> damageFlags, final double damage, final double elementUnits) {
        this(identity, source, damageType, elementType, damageComponents, damageFlags, damage, elementUnits, Key.empty(), 0);
    }
    
    public DamageSourceImpl(@NotNull DamageSourceIdentity damageSourceIdentity, @Nullable HariantEntity source, @NotNull DamageType damageType, @NotNull ElementType elementType, @NotNull List<? extends DamageComponent> damageComponents, @NotNull Set<DamageFlag> damageFlags, double damage, double elementUnits, @NotNull Cooldown cooldown) {
        this(damageSourceIdentity, source, damageType, elementType, damageComponents, damageFlags, damage, elementUnits, cooldown.getCooldownKey(), cooldown.getCooldown());
    }
    // #render
    
    @NotNull
    @Override
    public DamageSourceIdentity getIdentity() {
        return identity;
    }
    
    @NotNull
    @Override
    public ElementType getElementType() {
        return elementType;
    }
    
    @Nullable
    @Override
    public HariantEntity getSource() {
        return source;
    }
    
    @Override
    public double getElementUnits() {
        return elementUnits;
    }
    
    @NonNull
    @Override
    public Key getCooldownKey() {
        return cooldownKey;
    }
    
    @Override
    public int getCooldown() {
        return cooldown;
    }
    
    @NotNull
    @Override
    public DamageType getDamageType() {
        return damageType;
    }
    
    @Override
    public @NotNull List<? extends DamageComponent> getDamageComponents() {
        return damageComponents;
    }
    
    @Override
    public @NotNull Set<? extends DamageFlag> getDamageFlags() {
        return damageFlags;
    }
    
    @Override
    public double getDamage() {
        return damage;
    }
    
    @Override
    public boolean isFlagged(@NotNull DamageFlag damageFlag) {
        return damageFlags.contains(damageFlag);
    }
    
}