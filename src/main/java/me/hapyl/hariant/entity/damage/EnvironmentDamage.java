package me.hapyl.hariant.entity.damage;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnvironmentDamage implements DamageSource {
    
    private static final List<DamageComponent> DAMAGE_COMPONENTS = List.of(DamageComponent.elemental(), DamageComponent.defense());
    private static final Key COOLDOWN_KEY = Key.ofString("environment_cooldown");
    
    private final DamageSourceIdentity identity;
    private final ElementType elementType;
    private final double damage;
    
    EnvironmentDamage(@NotNull DamageSourceIdentity identity, @NotNull ElementType elementType, double damage) {
        this.identity = identity;
        this.elementType = elementType;
        this.damage = damage;
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
    public DamageType getDamageType() {
        return DamageType.ENVIRONMENT;
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
    public ElementType getElementType() {
        return elementType;
    }
    
    @NotNull
    @Override
    public List<? extends DamageComponent> getDamageComponents() {
        return DAMAGE_COMPONENTS;
    }
    
    @NotNull
    @Override
    public List<? extends DamageFlag> getDamageFlags() {
        return List.of();
    }
    
    @Override
    public double getDamage() {
        return damage;
    }
    
}
