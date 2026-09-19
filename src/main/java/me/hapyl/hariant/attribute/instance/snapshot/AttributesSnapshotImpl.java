package me.hapyl.hariant.attribute.instance.snapshot;

import com.google.common.collect.Sets;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public final class AttributesSnapshotImpl extends Attributes implements AttributesSnapshot {
    
    private final HariantEntity entity;
    private final Set<AttributeModifierSnapshot> modifiers;
    
    AttributesSnapshotImpl(@NotNull HariantEntity entity) {
        // Create a defensive copy and "snapshot" the current attribute values into base
        super(entity.getAttributes());
        
        this.entity = entity;
        this.modifiers = Sets.newHashSet();
    }
    
    @Override
    public double get(@NotNull AttributeType attributeType) {
        final double value = super.get(attributeType);
        double multiplier = 1;
        
        for (AttributeModifierSnapshot modifier : modifiers) {
            multiplier += modifier.getMultiplier(attributeType);
        }
        
        return attributeType.clamp(value * multiplier);
    }
    
    @Override
    public @NotNull Optional<HariantEntity> entity() {
        return Optional.of(entity);
    }
    
    @Override
    public @Nullable HariantEntity entityOrNull() {
        return entity;
    }
    
    @Override
    public void addModifier(@NotNull AttributeModifierSnapshot attributeModifier) {
        this.modifiers.add(attributeModifier);
    }
    
}
