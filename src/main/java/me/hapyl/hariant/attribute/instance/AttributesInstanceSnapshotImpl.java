package me.hapyl.hariant.attribute.instance;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class AttributesInstanceSnapshotImpl implements AttributesInstanceSnapshot {
    
    private final AttributesInstance attributes;
    
    AttributesInstanceSnapshotImpl(@NotNull HariantEntity entity) {
        // Create a defensive copy and "snapshot" the current attribute values into base
        this.attributes = new AttributesInstance(entity, entity.getAttributes());
    }
    
    @NotNull
    @Override
    public Optional<HariantEntity> getEntity() {
        return Optional.of(attributes.getEntity());
    }
    
    @Override
    public double get(@NotNull AttributeType attributeType) {
        return attributes.get(attributeType);
    }
    
    @Override
    public double base(@NotNull AttributeType attributeType) {
        return attributes.base(attributeType);
    }
    
    @Override
    public void set(@NotNull AttributeType attributeType, double value) {
        attributes.set(attributeType, value);
    }
    
    @Override
    public void addModifier(@NotNull AttributeModifier attributeModifier) {
        attributes.addModifier(attributeModifier);
    }
    
    @Override
    public boolean removeModifier(@NotNull Key key) {
        // Removing modifiers isn't supported
        return false;
    }
    
    @Override
    public boolean removeModifiers(@NotNull Predicate<AttributeModifier> filter) {
        // Removing modifiers isn't supported
        return false;
    }
    
    @Override
    public boolean hasModifier(@NotNull Key key) {
        return false;
    }
    
    @Override
    @NotNull
    public List<? extends AttributeModifier> getModifiers() {
        return attributes.getModifiers();
    }
    
    @NotNull
    @Override
    public Optional<AttributeModifier> getModifier(@NotNull Key key) {
        return attributes.getModifier(key);
    }
    
    @NotNull
    @Override
    public <M extends AttributeModifier> Optional<M> getModifier(@NotNull Class<M> modifierClass) {
        return attributes.getModifier(modifierClass);
    }
    
    @Override
    @NotNull
    public Stream<AttributeModifier> streamModifiers() {
        return Stream.empty();
    }
}
