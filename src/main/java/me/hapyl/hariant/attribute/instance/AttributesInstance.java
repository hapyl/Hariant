package me.hapyl.hariant.attribute.instance;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifiable;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierMap;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.util.Resettable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AttributesInstance extends Attributes implements AttributeModifiable, Ticking, Resettable {
    
    protected final HariantEntity entity;
    protected final AttributeModifierMap modifierMap;
    
    public AttributesInstance(@NotNull HariantEntity entity, @Nullable Attributes copyFrom) {
        super(copyFrom);
        
        this.entity = entity;
        this.modifierMap = new AttributeModifierMap(this);
    }
    
    @NotNull
    public HariantEntity getEntity() {
        return entity;
    }
    
    @Override
    public double get(@NotNull AttributeType attributeType) {
        final double value = super.get(attributeType);
        
        final double flatBonus = getFlatModifierBonus(attributeType);
        final double additiveBonus = getAdditiveModifierBonus(attributeType);
        final double multiplicativeBonus = getMultiplicativeModifierBonus(attributeType);
        
        return attributeType.clamp((value + flatBonus) * additiveBonus * multiplicativeBonus);
    }
    
    @Override
    public void addModifier(@NotNull AttributeModifier attributeModifier) {
        modifierMap.addModifier(attributeModifier);
    }
    
    @Override
    public boolean removeModifier(@NotNull Key key) {
        return modifierMap.removeModifier(key);
    }
    
    @Override
    public boolean removeModifiers(@NotNull Predicate<AttributeModifier> filter) {
        return modifierMap.removeModifiers(filter);
    }
    
    @Override
    public boolean hasModifier(@NotNull Key key) {
        return modifierMap.hasModifier(key);
    }
    
    @NotNull
    public List<? extends AttributeModifier> getModifiers() {
        return modifierMap.getModifiers();
    }
    
    @NotNull
    @Override
    public Optional<AttributeModifier> getModifier(@NotNull Key key) {
        return modifierMap.getModifier(key);
    }
    
    @NotNull
    @Override
    public <M extends AttributeModifier> Optional<M> getModifier(@NotNull Class<M> modifierClass) {
        return modifierMap.getModifier(modifierClass);
    }
    
    @Override
    public void tick() {
        modifierMap.tick();
    }
    
    @Override
    @NotNull
    public Stream<AttributeModifier> streamModifiers() {
        return modifierMap.streamModifiers();
    }
    
    public double getFlatModifierBonus(@NotNull AttributeType attributeType) {
        return modifierMap.streamOf(attributeType, AttributeModifierType.FLAT)
                          .mapToDouble(AttributeModifier.Entry::value)
                          .sum();
    }
    
    public double getAdditiveModifierBonus(@NotNull AttributeType attributeType) {
        return 1 + modifierMap.streamOf(attributeType, AttributeModifierType.ADDITIVE)
                              .mapToDouble(AttributeModifier.Entry::value)
                              .sum();
    }
    
    public double getMultiplicativeModifierBonus(@NotNull AttributeType attributeType) {
        return modifierMap.streamOf(attributeType, AttributeModifierType.MULTIPLICATIVE)
                          .mapToDouble(entry -> 1 + entry.value())
                          .reduce(1, (value1, value2) -> value1 * value2);
    }
    
    @Override
    public void reset() {
        modifierMap.reset();
    }
    
    public void updateAttribute(@NotNull AttributeType attributeType) {
        attributeType.update(entity, get(attributeType));
    }
    
}
