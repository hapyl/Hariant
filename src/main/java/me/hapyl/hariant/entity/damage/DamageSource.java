package me.hapyl.hariant.entity.damage;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.annotate.Immutable;
import me.hapyl.eterna.module.annotate.SelfReturn;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Buildable;
import me.hapyl.hariant.element.ElementSource;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.cooldown.Cooldown;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.entity.player.HariantPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Arrays;
import java.util.List;

public interface DamageSource extends DamageFlagged, Cooldown, ElementSource {
    
    @NotNull
    DamageSourceIdentity getIdentity();
    
    @Nullable
    @Override
    HariantEntity getSource();
    
    @NotNull
    @Override
    ElementType getElementType();
    
    @Range(from = 0, to = Integer.MAX_VALUE)
    @Override
    default double getElementUnits() {
        return 0;
    }
    
    @NotNull
    default Key getCooldownKey() {
        return Key.empty();
    }
    
    @Override
    default int getCooldown() {
        return 0;
    }
    
    @NotNull
    DamageType getDamageType();
    
    @NotNull
    List<? extends DamageComponent> getDamageComponents();
    
    @NotNull
    @Immutable
    List<? extends DamageFlag> getDamageFlags();
    
    double getDamage();
    
    @NotNull
    default Builder toBuilder(final double newDamage) {
        final Builder builder = new Builder(this.getIdentity(), newDamage);
        builder.source = this.getSource();
        builder.damageType = this.getDamageType();
        builder.elementType = this.getElementType();
        builder.damageComponents.addAll(this.getDamageComponents());
        builder.damageFlags.addAll(this.getDamageFlags());
        return builder;
    }
    
    @NotNull
    default Builder toBuilder() {
        return toBuilder(this.getDamage());
    }
    
    @NotNull
    static Builder builder(@NotNull DamageSourceIdentity identity, final double damage) {
        return new Builder(identity, damage);
    }
    
    @NotNull
    static Builder death(@NotNull DamageSourceIdentity identity) {
        return new Builder(identity, 1); // Damage does not matter it's meant to be used in die()
    }
    
    @NotNull
    static Builder common(@NotNull DamageSourceIdentity identity, final double damage) {
        return new Builder(identity, damage).components(DamageComponent.common());
    }
    
    class Builder implements Buildable<DamageSource> {
        
        private final DamageSourceIdentity identity;
        private final double damage;
        
        private final List<DamageComponent> damageComponents;
        private final List<DamageFlag> damageFlags;
        
        private HariantEntity source;
        private ElementType elementType;
        private DamageType damageType;
        
        private double elementUnits;
        
        Builder(@NotNull DamageSourceIdentity identity, final double damage) {
            this.identity = identity;
            this.damage = damage;
            this.damageComponents = Lists.newArrayList();
            this.damageFlags = Lists.newArrayList();
            this.source = null;
            this.elementType = ElementType.PHYSICAL;
            this.damageType = DamageType.MELEE;
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
        public Builder component(@NotNull DamageComponent component) {
            this.damageComponents.add(component);
            return this;
        }
        
        @SelfReturn
        public Builder components(@NotNull List<? extends DamageComponent> components) {
            this.damageComponents.clear();
            this.damageComponents.addAll(components);
            return this;
        }
        
        @SelfReturn
        public <C extends DamageComponent> Builder replaceComponent(@NotNull Class<C> componentClass, @NotNull C replacement) {
            this.damageComponents.replaceAll(component -> componentClass.isInstance(component) ? replacement : component);
            return this;
        }
        
        @SelfReturn
        public Builder damageFlag(@NotNull DamageFlag... flags) {
            this.damageFlags.clear();
            this.damageFlags.addAll(Arrays.asList(flags));
            return this;
        }
        
        @SelfReturn
        public Builder units(double units) {
            this.elementUnits = units;
            return this;
        }
        
        @NotNull
        @Override
        public DamageSource build() {
            return new DamageSourceImpl(identity, source, damageType, elementType, List.copyOf(damageComponents), List.copyOf(damageFlags), damage, elementUnits);
        }
    }
}
