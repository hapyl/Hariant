package me.hapyl.hariant.entity.damage;

import me.hapyl.eterna.module.annotate.SelfReturn;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Buildable;
import me.hapyl.hariant.element.ElementSource;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.cooldown.HariantCooldown;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Set;

public interface DamageSource extends DamageFlagged, HariantCooldown, ElementSource {
    
    @NotNull DamageSourceIdentity getIdentity();
    
    @Override
    @NotNull ElementType getElementType();
    
    @Override
    @Nullable HariantEntity getSource();
    
    @Range(from = 0, to = Integer.MAX_VALUE)
    @Override
    default double getElementUnits() {
        return 0;
    }
    
    default @NotNull Key getCooldownKey() {
        return Key.empty();
    }
    
    @Override
    default int getCooldown() {
        return 0;
    }
    
    @NotNull DamageType getDamageType();
    
    @NotNull List<? extends DamageComponent> getDamageComponents();
    
    @Unmodifiable
    @NotNull Set<? extends DamageFlag> getDamageFlags();
    
    double getDamage();
    
    default boolean compareIdentity(@NotNull DamageSourceIdentity identity) {
        return this.getIdentity().equals(identity);
    }
    
    default @NotNull Builder toBuilder(final double newDamage) {
        final Builder builder = new Builder(this.getIdentity(), newDamage);
        builder.source = this.getSource();
        builder.damageType = this.getDamageType();
        builder.elementType = this.getElementType();
        builder.damageComponents = this.getDamageComponents();
        builder.damageFlags = this.getDamageFlags();
        builder.cooldownKey = this.getCooldownKey();
        builder.cooldown = this.getCooldown();
        return builder;
    }
    
    default @NotNull Builder toBuilder() {
        return toBuilder(this.getDamage());
    }
    
    default void startCooldownIfExists(@NotNull HariantEntity hariantEntity) {
        if (hasCooldown()) {
            // Set the damage cooldown, which isn't scaled by any attribute
            hariantEntity.setCooldown(this, getCooldown(), null);
        }
    }
    
    default boolean canTriggerFerocity() {
        return switch (this.getDamageType()) {
            case MELEE, RANGED -> true;
            default -> false;
        };
    }
    
    @NotNull
    static Builder builder(@NotNull DamageSourceIdentity identity, final double damage) {
        return new Builder(identity, damage);
    }
    
    @NotNull
    static Builder death(@NotNull DamageSourceIdentity identity) {
        return new Builder(identity, 1);
    }
    
    @NotNull
    static Builder common(@NotNull DamageSourceIdentity identity, final double damage) {
        return new Builder(identity, damage).components(DamageComponent.ofCommon());
    }
    
    class Builder implements Buildable<DamageSource> {
        
        private final @NotNull DamageSourceIdentity identity;
        private final double damage;
        
        private @NotNull @Unmodifiable List<? extends DamageComponent> damageComponents;
        private @NotNull @Unmodifiable Set<? extends DamageFlag> damageFlags;
        
        private @Nullable HariantEntity source;
        private @NotNull ElementType elementType;
        private @NotNull DamageType damageType;
        
        private double elementUnits;
        
        private @NotNull Key cooldownKey;
        private int cooldown;
        
        Builder(@NotNull DamageSourceIdentity identity, final double damage) {
            this.identity = identity;
            this.damage = damage;
            this.damageComponents = List.of();
            this.damageFlags = Set.of();
            this.source = null;
            this.elementType = ElementType.PHYSICAL;
            this.damageType = DamageType.MELEE;
            this.cooldownKey = Key.empty();
            this.cooldown = 0;
        }
        
        @SelfReturn
        public Builder source(@Nullable HariantEntity attacker) {
            this.source = attacker;
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
        public Builder components(@NotNull List<? extends DamageComponent> components) {
            this.damageComponents = components;
            return this;
        }
        
        @SelfReturn
        public Builder damageFlags(@NotNull DamageFlag... flags) {
            this.damageFlags = Set.of(flags);
            return this;
        }
        
        @SelfReturn
        public Builder elementalUnits(double units) {
            this.elementUnits = units;
            return this;
        }
        
        @SelfReturn
        public Builder cooldown(@NotNull Key cooldownKey, int cooldown) {
            this.cooldownKey = cooldownKey;
            this.cooldown = cooldown;
            return this;
        }
        
        @SelfReturn
        public Builder cooldownSeconds(@NotNull Key cooldownKey, float cooldownSeconds) {
            return cooldown(cooldownKey, (int) (cooldownSeconds * 20));
        }
        
        @NotNull
        @Override
        public DamageSource build() {
            return new DamageSourceImpl(identity, source, damageType, elementType, damageComponents, damageFlags, damage, elementUnits, cooldownKey, cooldown);
        }
    }
    
}