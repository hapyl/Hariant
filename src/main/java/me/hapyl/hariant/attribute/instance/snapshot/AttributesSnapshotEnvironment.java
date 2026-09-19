package me.hapyl.hariant.attribute.instance.snapshot;

import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Represents an empty {@link AttributesSnapshot} as a substitute for non-damager damage instances;
 * cannot be mutated and always returns {@code 0} for any {@link AttributeType}.
 */
public final class AttributesSnapshotEnvironment implements AttributesSnapshot {
    
    static final AttributesSnapshot INSTANCE = new AttributesSnapshotEnvironment();
    
    private AttributesSnapshotEnvironment() {
    }
    
    @Override
    public @NotNull Optional<HariantEntity> entity() {
        return Optional.empty();
    }
    
    @Override
    public @Nullable HariantEntity entityOrNull() {
        return null;
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
    public void addModifier(@NotNull AttributeModifierSnapshot attributeModifier) {
    }
    
}