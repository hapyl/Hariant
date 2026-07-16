package me.hapyl.hariant.attribute.instance;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public final class AttributesInstanceSnapshotEnvironment implements AttributesInstanceSnapshot {
    
    static final AttributesInstanceSnapshot INSTANCE = new AttributesInstanceSnapshotEnvironment();
    
    AttributesInstanceSnapshotEnvironment() {
    }
    
    @NotNull
    @Override
    public Optional<HariantEntity> entity() {
        return Optional.empty();
    }
    
    @Override
    public double get(@NotNull AttributeType attributeType) {
        return 0;
    }
    
    @Override
    public double base(@NotNull AttributeType attributeType) {
        return 0;
    }
    
    @Override
    public void set(@NotNull AttributeType attributeType, double value) {
    }
    
    @Override
    public void add(@NotNull AttributeType attributeType, double value) {
    }
    
    @Override
    public void addModifier(@NotNull AttributeModifier attributeModifier) {
    }
    
    @Override
    public boolean removeModifier(@NotNull Key key) {
        return false;
    }
    
    @Override
    public boolean hasModifier(@NotNull Key key) {
        return false;
    }
    
    @Override
    @NotNull
    public List<? extends AttributeModifier> getModifiers() {
        return List.of();
    }
    
    @NotNull
    @Override
    public Optional<AttributeModifier> getModifier(@NotNull Key key) {
        return Optional.empty();
    }
    
    @NotNull
    @Override
    public <M extends AttributeModifier> Optional<M> getModifier(@NotNull Class<M> modifierClass) {
        return Optional.empty();
    }
    
    @Override
    @NotNull
    public Stream<AttributeModifier> streamModifiers() {
        return Stream.empty();
    }
    
}
