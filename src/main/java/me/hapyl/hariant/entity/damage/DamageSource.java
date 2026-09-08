package me.hapyl.hariant.entity.damage;

import me.hapyl.eterna.module.annotate.NotEmpty;
import me.hapyl.eterna.module.annotate.SelfReturn;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Buildable;
import me.hapyl.hariant.element.ElementSource;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.cooldown.HariantCooldown;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.util.ImmutableCollectionMerger;
import org.jetbrains.annotations.*;

import java.util.List;
import java.util.Set;

/**
 * {@link DamageSource} is the base interface used for damage calculations, it stores an <b>immutable</b> base information about the damage,
 * which includes {@link DamageSourceIdentity}, base damage, {@link DamageComponent}, etc.
 *
 * <p>
 * It is then fed to {@link DamageInstance}, which creates a "copy" of the source, allowing mutating the damage instance when needed, while
 * preserving the immutability of damage source.
 * </p>
 *
 * <p>
 * It is recommended to create a single damage source when multiple damage instances must deal the same "type" of damage, which will introduce
 * a "snapshot" mechanic that stores the damage information once.
 * </p>
 */

public interface DamageSource extends DamageFlagged, HariantCooldown, ElementSource {
    
    @NotNull DamageSourceIdentity getIdentity();
    
    @Override
    @NotNull ElementType getElementType();
    
    @Override
    @Nullable HariantEntity getSource();
    
    @Range(from = 0, to = Integer.MAX_VALUE)
    @Override
    double getElementUnits();
    
    @NotNull Key getCooldownKey();
    
    @Override
    int getCooldown();
    
    @NotNull DamageType getDamageType();
    
    @NotNull List<? extends DamageComponent> getDamageComponents();
    
    @Unmodifiable
    @NotNull Set<? extends DamageFlag> getDamageFlags();
    
    double getDamage();
    
    static @NotNull DamageSource death(@NotNull DamageSourceIdentity damageSourceIdentity, @Nullable HariantEntity source) {
        return new DamageSourceImpl(damageSourceIdentity, source, DamageType.MELEE, ElementType.PHYSICAL, List.of(), Set.of(), 1, 0);
    }
    
    static @NotNull DamageSource death(@NotNull DamageSourceIdentity damageSourceIdentity) {
        return death(damageSourceIdentity, null);
    }
    
    /**
     * Creates a new clean, mutable {@link Builder} instance that allows mutation of the underlying {@link DamageSource}.
     *
     * <p>
     * Note that the only initial values of the builder are {@link DamageSourceIdentity} and {@code damage}, everything else is assigned
     * to either {@code null} or {@code empty} collection; therefore, you must manually set all the fields, including {@link DamageComponent}.
     * </p>
     *
     * @param damageSourceIdentity - The damage source identity.
     * @param damage               - The initial damage.
     * @return a new builder.
     * @apiNote Note that using the builder has one major downside - {@code instanceof} are impossible since it builds a generic {@link DamageSource},
     * therefore, using builder should generally be avoided in favor of a statically named class that can be instanced when needed.
     */
    @ApiStatus.Experimental
    static @NotNull Builder builder(@NotNull DamageSourceIdentity damageSourceIdentity, final double damage) {
        return new Builder(damageSourceIdentity, damage);
    }
    
    @ApiStatus.Experimental
    class Builder implements Buildable<DamageSource> {
        
        private final DamageSourceIdentity damageSourceIdentity;
        private final double damage;
        
        private @Nullable HariantEntity source;
        
        private @NotNull DamageType damageType;
        private @NotNull ElementType elementType;
        
        private @NotNull @Unmodifiable List<? extends DamageComponent> damageComponents;
        private @NotNull @Unmodifiable Set<? extends DamageFlag> damageFlags;
        
        private double units;
        
        private @NotNull Key cooldownKey;
        private int cooldown;
        
        Builder(@NotNull DamageSourceIdentity damageSourceIdentity, final double damage) {
            this.damageSourceIdentity = damageSourceIdentity;
            this.damage = damage;
            this.source = null;
            this.damageType = DamageType.MELEE;
            this.elementType = ElementType.PHYSICAL;
            this.damageComponents = List.of();
            this.damageFlags = Set.of();
            this.units = 0;
            this.cooldownKey = Key.empty();
        }
        
        @SelfReturn
        public Builder source(@NotNull HariantEntity source) {
            this.source = source;
            return this;
        }
        
        @SelfReturn
        public Builder damageType(@NotNull DamageType damageType) {
            this.damageType = damageType;
            return this;
        }
        
        @SelfReturn
        public Builder elementType(@NotNull ElementType elementType) {
            this.elementType = elementType;
            return this;
        }
        
        @SelfReturn
        public Builder damageComponents(@NotNull @Unmodifiable List<? extends DamageComponent> damageComponents, @NotNull Strategy strategy) {
            this.damageComponents = strategy == Strategy.MERGE ? ImmutableCollectionMerger.merge(this.damageComponents, damageComponents) : damageComponents;
            return this;
        }
        
        @SelfReturn
        public Builder damageFlags(@NotNull Set<? extends DamageFlag> damageFlags, @NotNull Strategy strategy) {
            this.damageFlags = strategy == Strategy.MERGE ? ImmutableCollectionMerger.merge(this.damageFlags, damageFlags) : damageFlags;
            return this;
        }
        
        @SelfReturn
        public Builder elementalUnits(final double units) {
            this.units = units;
            return this;
        }
        
        @SelfReturn
        public Builder cooldown(@NotNull @NotEmpty Key cooldownKey, final int cooldown) {
            this.cooldownKey = cooldownKey;
            this.cooldown = cooldown;
            return this;
        }
        
        @Override
        public @NotNull DamageSource build() {
            return new DamageSourceImpl(damageSourceIdentity, source, damageType, elementType, damageComponents, damageFlags, damage, units, cooldownKey, cooldown);
        }
        
    }
    
    /**
     * Represents a strategy for {@link DamageSource.Builder} damage components and damage flags setter.
     */
    enum Strategy {
        
        /**
         * Defines that the given argument replaces the existing one.
         */
        REPLACE,
        
        /**
         * Defines that the given arguments is merged with the existing one.
         */
        MERGE
        
    }
    
}