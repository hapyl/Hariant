package me.hapyl.hariant.attribute.modifier;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Streamable;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstance;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.effect.EffectType;
import me.hapyl.hariant.event.HariantAttributeEvent;
import me.hapyl.hariant.util.Resettable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class AttributeModifierMap implements AttributeModifiable, Ticking, Streamable<AttributeModifier.Entry>, Resettable {
    
    private final AttributesInstance attributesInstance;
    private final HariantEntity entity;
    
    private final Map<Key, AttributeModifier> modifiers;
    
    public AttributeModifierMap(@NotNull AttributesInstance attributesInstance) {
        this.attributesInstance = attributesInstance;
        this.entity = attributesInstance.getEntity();
        this.modifiers = Maps.newLinkedHashMap(); // Storing order literally just for display purpose
    }
    
    @Override
    @NotNull
    public Stream<AttributeModifier> streamModifiers() {
        return modifiers.values().stream();
    }
    
    @Override
    @NotNull
    public List<? extends AttributeModifier> getModifiers() {
        return List.copyOf(modifiers.values());
    }
    
    @Override
    public void tick() {
        final Iterator<AttributeModifier> iterator = modifiers.values().iterator();
        
        while (iterator.hasNext()) {
            final AttributeModifier modifier = iterator.next();
            
            // Tick modifiers
            modifier.tick(attributesInstance.getEntity());
            
            if (modifier.isOver()) {
                modifier.onRemove0(entity);
                iterator.remove();
                this.triggerAttributeUpdate(modifier);
            }
        }
    }
    
    @Override
    public void addModifier(@NotNull AttributeModifier attributeModifier) {
        // Check for effect resistance for negative modifiers
        final HariantEntity applier = attributeModifier.getApplier();
        
        if (attributeModifier.getEffectType() == EffectType.DEBUFF && entity.hasEffectResistance(applier)) {
            return;
        }
        
        final boolean modifierExists = this.modifiers.containsKey(attributeModifier.getKey());
        
        this.modifiers.put(attributeModifier.getKey(), attributeModifier);
        this.triggerAttributeUpdate(attributeModifier);
        
        // Call `onApply`
        attributeModifier.onApply(entity, applier);
        
        // If new modifier, trigger component display
        if (!modifierExists) {
            attributeModifier.display(entity.getMidpointLocation());
        }
        
        // Trigger buff/debuff
        new HariantAttributeEvent(entity, attributeModifier).callEvent();
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
        
        attributesToUpdate.forEach(attributesInstance::updateAttribute);
        return modified;
    }
    
    @Override
    public boolean hasModifier(@NotNull Key key) {
        return modifiers.containsKey(key);
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
    
    @NotNull
    @Override
    public Stream<AttributeModifier.Entry> stream() {
        return modifiers.values().stream().flatMap(AttributeModifier::stream);
    }
    
    @NotNull
    public Stream<AttributeModifier.Entry> streamOf(@NotNull AttributeType attributeType, @NotNull AttributeModifierType modifierType) {
        return stream().filter(entry -> entry.attributeType() == attributeType && entry.modifierType() == modifierType);
    }
    
    @Override
    public void reset() {
        modifiers.clear();
    }
    
    private void triggerAttributeUpdate(@NotNull AttributeModifier modifier) {
        modifier.stream()
                .map(AttributeModifier.Entry::attributeType)
                .distinct()
                .forEach(attributesInstance::updateAttribute);
    }
    
}
