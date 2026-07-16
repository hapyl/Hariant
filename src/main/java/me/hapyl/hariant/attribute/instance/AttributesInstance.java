package me.hapyl.hariant.attribute.instance;

import com.google.common.collect.Maps;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class AttributesInstance extends Attributes implements AttributeModifiable, Ticking, Resettable {
    
    protected final HariantEntity entity;
    protected final Map<Key, AttributeModifier> modifiers;
    
    public AttributesInstance(@NotNull HariantEntity entity, @Nullable Attributes copyFrom) {
        super(copyFrom);
        
        this.entity = entity;
        this.modifiers = Maps.newLinkedHashMap();    // Keep order, literally just for the display purpose
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
        final AttributeModifier attributeModifier = modifiers.remove(key);
        
        if (attributeModifier == null) {
            return false;
        }
        
        attributeModifier.onRemove0(entity);
        this.triggerAttributeUpdate(attributeModifier);
        
        return true;
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
    @NotNull
    public Stream<AttributeModifier> streamModifiers() {
        return modifiers.values().stream();
    }
    
    @Override
    public void tick() {
        // Tick modifiers over a defensive copy
        for (AttributeModifier attributeModifier : Set.copyOf(modifiers.values())) {
            // Tick modifier
            attributeModifier.tick(entity);
            
            // If modifier is over, remove it
            if (attributeModifier.isOver()) {
                this.removeModifier(attributeModifier.getKey());
            }
        }
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
