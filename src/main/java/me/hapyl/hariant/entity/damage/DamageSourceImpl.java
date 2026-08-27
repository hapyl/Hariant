package me.hapyl.hariant.entity.damage;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.cooldown.HariantCooldown;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DamageSourceImpl implements DamageSource {
    
    private final @NotNull DamageSourceIdentity identity;
    private final double damage;
    
    private @Nullable HariantEntity source;
    
    private @NotNull ElementType elementType;
    private @NotNull DamageType damageType;
    
    private @NotNull @Unmodifiable List<? extends DamageComponent> damageComponents;
    private @NotNull @Unmodifiable Set<? extends DamageFlag> damageFlags;
    
    private double elementUnits;
    
    private @NotNull Key cooldownKey;
    private int cooldown;
    
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
    
    public DamageSourceImpl(@NotNull DamageSourceIdentity damageSourceIdentity, @Nullable HariantEntity source, @NotNull DamageType damageType, @NotNull ElementType elementType, @NotNull List<? extends DamageComponent> damageComponents, @NotNull Set<DamageFlag> damageFlags, double damage, double elementUnits, @NotNull HariantCooldown cooldown) {
        this(damageSourceIdentity, source, damageType, elementType, damageComponents, damageFlags, damage, elementUnits, cooldown.getCooldownKey(), cooldown.getCooldown());
    }
    
    public DamageSourceImpl(@NotNull DamageSourceIdentity identity, double damage) {
        this(identity, null, DamageType.MELEE, ElementType.PHYSICAL, List.of(), Set.of(), damage, 0, Key.empty(), 0);
    }
    
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
    
    @Override
    public void setElementType(@NotNull ElementType elementType) {
        this.elementType = elementType;
    }
    
    @Nullable
    @Override
    public HariantEntity getSource() {
        return source;
    }
    
    @Override
    public void setSource(@Nullable HariantEntity source) {
        this.source = source;
    }
    
    @Override
    public double getElementUnits() {
        return elementUnits;
    }
    
    @Override
    public void setElementUnits(double units) {
        this.elementUnits = units;
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
    
    @Override
    public void setCooldown(@NotNull Key key, int cooldown) {
        this.cooldownKey = key;
        this.cooldown = cooldown;
    }
    
    @NotNull
    @Override
    public DamageType getDamageType() {
        return damageType;
    }
    
    @Override
    public void setDamageType(@NotNull DamageType damageType) {
        this.damageType = damageType;
    }
    
    @Override
    public @NotNull List<? extends DamageComponent> getDamageComponents() {
        return damageComponents;
    }
    
    @Override
    public void setDamageComponents(@NotNull List<? extends DamageComponent> damageComponents) {
        this.damageComponents = damageComponents;
    }
    
    @Override
    public @NotNull Set<? extends DamageFlag> getDamageFlags() {
        return damageFlags;
    }
    
    @Override
    public void setDamageFlags(@NotNull Set<? extends DamageFlag> damageFlags) {
        this.damageFlags = damageFlags;
    }
    
    @Override
    public double getDamage() {
        return damage;
    }
    
    @Override
    public @NotNull DamageSource clone() {
        try {
            // I'm aware that `clone` is considered a bad design in Java, but after a long chat
            // with different AIs and myself, I've decided to use it here.
            //
            // It's mainly used for Ferocity, since it's purpose it to literally `clone` the attack,
            // and clone fits here the best, since it keeps the object instance, which is lost any
            // other way I tried, which is important because most events rely on `instanceof` check
            // of the damage source of the damage instance.
            final DamageSourceImpl clone = (DamageSourceImpl) super.clone();
            
            clone.damageComponents = List.copyOf(damageComponents);
            clone.damageFlags = Set.copyOf(damageFlags);
            
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public boolean isFlagged(@NotNull DamageFlag damageFlag) {
        return damageFlags.contains(damageFlag);
    }
    
    @Override
    public final int hashCode() {
        return Objects.hashCode(this.identity);
    }
    
    @Override
    public final boolean equals(@Nullable Object object) {
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        
        final DamageSourceImpl that = (DamageSourceImpl) object;
        return Objects.equals(this.identity, that.identity);
    }
    
}