package me.hapyl.hariant.attribute.instance;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierAdderHandler;
import me.hapyl.hariant.entity.HariantEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class AttributesInstanceSnapshotImpl extends AttributesInstance implements AttributesInstanceSnapshot {
    
    AttributesInstanceSnapshotImpl(@NotNull HariantEntity entity) {
        // Create a defensive copy and "snapshot" the current attribute values into base
        super(entity, entity.getAttributes());
    }
    
    @NotNull
    @Override
    public Optional<HariantEntity> entity() {
        return Optional.of(entity);
    }
    
    @Override
    public void addModifier(@NotNull AttributeModifier attributeModifier) {
        // Skip any entity checks, directly add the modifier without triggering anything
        modifiers.put(attributeModifier.getKey(), attributeModifier);
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
        return super.hasModifier(key);
    }
    
    @Override
    @NotNull
    public List<? extends AttributeModifier> getModifiers() {
        return super.getModifiers();
    }
    
    @NotNull
    @Override
    public Optional<AttributeModifier> getModifier(@NotNull Key key) {
        return super.getModifier(key);
    }
    
    @NotNull
    @Override
    public <M extends AttributeModifier> Optional<M> getModifier(@NotNull Class<M> modifierClass) {
        return super.getModifier(modifierClass);
    }
    
    @Override
    @NotNull
    public Stream<AttributeModifier> streamModifiers() {
        return super.streamModifiers();
    }
}
