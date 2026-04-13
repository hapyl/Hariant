package me.hapyl.hariant.entity.damage;

import me.hapyl.eterna.module.annotate.Immutable;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class DamageSourceImpl implements DamageSource {
    
    @Nullable private final HariantEntity source;
    
    @NotNull private final DamageSourceIdentity identity;
    @NotNull private final ElementType elementType;
    @NotNull private final DamageType damageType;
    @NotNull private final List<DamageComponent> damageComponents;
    @NotNull private final List<DamageFlag> damageFlags;
    
    private final double damage;
    private final double elementUnits;
    
    public DamageSourceImpl(
            @NotNull DamageSourceIdentity identity,
            @Nullable HariantEntity source,
            @NotNull DamageType damageType,
            @NotNull ElementType elementType,
            @NotNull @Immutable List<DamageComponent> damageComponents,
            @NotNull @Immutable List<DamageFlag> damageFlags,
            final double damage,
            final double elementUnits
    ) {
        this.identity = identity;
        this.source = source;
        this.elementType = elementType;
        this.damageType = damageType;
        this.damageComponents = damageComponents;
        this.damageFlags = damageFlags;
        this.damage = damage;
        this.elementUnits = elementUnits;
    }
    
    @NotNull
    @Override
    public DamageSourceIdentity getIdentity() {
        return identity;
    }
    
    @Nullable
    @Override
    public HariantEntity getSource() {
        return source;
    }
    
    @NotNull
    @Override
    public ElementType getElementType() {
        return elementType;
    }
    
    @Override
    public double getElementUnits() {
        return elementUnits;
    }
    
    @NotNull
    @Override
    public DamageType getDamageType() {
        return damageType;
    }
    
    @NotNull
    @Override
    public List<DamageComponent> getDamageComponents() {
        return damageComponents;
    }
    
    @NotNull
    @Override
    public List<DamageFlag> getDamageFlags() {
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
    
    @Override
    public String toString() {
        return "DamageSourceImpl{" +
               "source=" + source +
               ", elementType=" + elementType +
               ", damageType=" + damageType +
               ", damageComponents=" + damageComponents.stream().map(DamageComponent::identify).collect(Collectors.joining(", ")) +
               ", damageFlags=" + damageFlags +
               ", damage=" + damage +
               ", elementUnits=" + elementUnits +
               '}';
    }
}
