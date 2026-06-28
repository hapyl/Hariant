package me.hapyl.hariant.attribute.instance;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifiable;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.event.HariantEffectEvent;
import me.hapyl.hariant.util.Resettable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AttributesInstance extends Attributes implements AttributeModifiable, Ticking, Resettable {
    
    protected final HariantEntity entity;
    protected final Map<Key, AttributeModifier> modifiers;
    
    public AttributesInstance(@NotNull HariantEntity entity, @Nullable Attributes copyFrom) {
        super(copyFrom);
        
        this.entity = entity;
        this.modifiers = Maps.newLinkedHashMap(); // Keep order, literally just for the display purpose
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
        final HariantEntity applier = attributeModifier.getApplier();
        
        // Check for effect resistance for negative modifiers
        if (HariantEffectEvent.callEvent(entity, applier, attributeModifier)) {
            return;
        }
        
        final boolean modifierExists = this.modifiers.containsKey(attributeModifier.getKey());
        
        this.modifiers.put(attributeModifier.getKey(), attributeModifier);
        this.triggerAttributeUpdate(attributeModifier);
        
        // Call `onApply`
        attributeModifier.onApply(entity, applier, attributeModifier.duration());
        
        // If new modifier, trigger component display
        if (!modifierExists) {
            attributeModifier.display(entity.getMidpointLocation());
        }
    }
    
    @Override
    public boolean removeModifier(@NotNull Key key) {
        final AttributeModifier modifier = this.modifiers.remove(key);
        
        if (modifier != null) {
            modifier.onRemove0(entity);
            this.triggerAttributeUpdate(modifier);
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean removeModifiers(@NotNull Predicate<AttributeModifier> filter) {
        final Set<AttributeType> attributesToUpdate = Sets.newHashSet();
        
        final boolean modified = this.modifiers.values().removeIf(modifier -> {
            if (!filter.test(modifier)) {
                return false;
            }
            
            modifier.stream().map(AttributeModifier.Entry::attributeType).forEach(attributesToUpdate::add);
            modifier.onRemove0(entity);
            return true;
        });
        
        attributesToUpdate.forEach(this::updateAttribute);
        return modified;
    }
    
    @Override
    public boolean hasModifier(@NotNull Key key) {
        return modifiers.containsKey(key);
    }
    
    @NotNull
    public List<? extends AttributeModifier> getModifiers() {
        return List.copyOf(modifiers.values());
    }
    
    @NotNull
    @Override
    public Optional<AttributeModifier> getModifier(@NotNull Key key) {
        return Optional.ofNullable(modifiers.get(key));
    }
    
    @NotNull
    @Override
    public <M extends AttributeModifier> Optional<M> getModifier(@NotNull Class<M> modifierClass) {
        return modifiers.values()
                        .stream()
                        .filter(modifierClass::isInstance)
                        .map(modifierClass::cast)
                        .findFirst();
    }
    
    @Override
    public void tick() {
        final Iterator<AttributeModifier> iterator = modifiers.values().iterator();
        
        while (iterator.hasNext()) {
            final AttributeModifier modifier = iterator.next();
            
            // Tick modifier
            modifier.tick(entity);
            
            if (modifier.isOver()) {
                modifier.onRemove0(entity);
                iterator.remove();
                this.triggerAttributeUpdate(modifier);
            }
        }
    }
    
    @Override
    @NotNull
    public Stream<AttributeModifier> streamModifiers() {
        return modifiers.values().stream();
    }
    
    public double getFlatModifierBonus(@NotNull AttributeType attributeType) {
        return streamModifierEntries(attributeType, AttributeModifierType.FLAT)
                          .mapToDouble(AttributeModifier.Entry::value)
                          .sum();
    }
    
    public double getAdditiveModifierBonus(@NotNull AttributeType attributeType) {
        return 1 + streamModifierEntries(attributeType, AttributeModifierType.ADDITIVE)
                              .mapToDouble(AttributeModifier.Entry::value)
                              .sum();
    }
    
    public double getMultiplicativeModifierBonus(@NotNull AttributeType attributeType) {
        return streamModifierEntries(attributeType, AttributeModifierType.MULTIPLICATIVE)
                          .mapToDouble(entry -> 1 + entry.value())
                          .reduce(1, (value1, value2) -> value1 * value2);
    }
    
    @Override
    public void reset() {
        modifiers.clear();
    }
    
    public void updateAttribute(@NotNull AttributeType attributeType) {
        attributeType.update(entity, get(attributeType));
    }
    
    private void triggerAttributeUpdate(@NotNull AttributeModifier modifier) {
        modifier.stream()
                .map(AttributeModifier.Entry::attributeType)
                .distinct()
                .forEach(this::updateAttribute);
    }
    
    @NotNull
    private Stream<AttributeModifier.Entry> streamModifierEntries(@NotNull AttributeType attributeType, @NotNull AttributeModifierType modifierType) {
        return modifiers.values().stream().flatMap(AttributeModifier::stream).filter(entry -> entry.attributeType() == attributeType && entry.modifierType() == modifierType);
    }
    
}
