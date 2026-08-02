package me.hapyl.hariant.entity.damage;

import me.hapyl.eterna.module.annotate.SelfReturn;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Buildable;
import me.hapyl.eterna.module.util.Copyable;
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

public interface DamageSource extends DamageFlagged, HariantCooldown, ElementSource, Copyable {
    
    @NotNull DamageSourceIdentity getIdentity();
    
    @Override
    @NotNull ElementType getElementType();
    
    void setElementType(@NotNull ElementType elementType);
    
    @Override
    @Nullable HariantEntity getSource();
    
    void setSource(@Nullable HariantEntity source);
    
    @Range(from = 0, to = Integer.MAX_VALUE)
    @Override
    double getElementUnits();
    
    void setElementUnits(double units);
    
    @NotNull Key getCooldownKey();
    
    @Override
    int getCooldown();
    
    void setCooldown(@NotNull Key key, int cooldown);
    
    @NotNull DamageType getDamageType();
    
    void setDamageType(@NotNull DamageType damageType);
    
    @NotNull List<? extends DamageComponent> getDamageComponents();
    
    void setDamageComponents(@NotNull List<? extends DamageComponent> damageComponents);
    
    @Unmodifiable
    @NotNull Set<? extends DamageFlag> getDamageFlags();
    
    void setDamageFlags(@NotNull Set<? extends DamageFlag> damageFlags);
    
    double getDamage();
    
    @Override
    @NotNull DamageSource createCopy();
    
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
    
    class Builder implements Buildable<DamageSource> {
        
        private final DamageSource damageSource;
        
        Builder(@NotNull DamageSourceIdentity identity, final double damage) {
            this.damageSource = new DamageSourceImpl(identity, damage);
        }
        
        @SelfReturn
        public Builder source(@Nullable HariantEntity source) {
            this.damageSource.setSource(source);
            return this;
        }
        
        @SelfReturn
        public Builder damageType(@NotNull DamageType damageType) {
            this.damageSource.setDamageType(damageType);
            return this;
        }
        
        @SelfReturn
        public Builder elementType(@NotNull ElementType elementType) {
            this.damageSource.setElementType(elementType);
            return this;
        }
        
        @SelfReturn
        public Builder components(@NotNull List<? extends DamageComponent> components) {
            this.damageSource.setDamageComponents(components);
            return this;
        }
        
        @SelfReturn
        public Builder damageFlags(@NotNull DamageFlag... flags) {
            this.damageSource.setDamageFlags(Set.of(flags));
            return this;
        }
        
        @SelfReturn
        public Builder elementalUnits(double units) {
            this.damageSource.setElementUnits(units);
            return this;
        }
        
        @SelfReturn
        public Builder cooldown(@NotNull Key cooldownKey, int cooldown) {
            this.damageSource.setCooldown(cooldownKey, cooldown);
            return this;
        }
        
        @NotNull
        @Override
        public DamageSource build() {
            return damageSource;
        }
    }
    
}