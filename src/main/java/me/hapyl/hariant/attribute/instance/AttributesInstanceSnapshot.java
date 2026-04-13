package me.hapyl.hariant.attribute.instance;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifiable;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface AttributesInstanceSnapshot extends AttributesBase, AttributeModifiable {
    
    @NotNull
    Optional<HariantEntity> getEntity();
    
    @Override
    double get(@NotNull AttributeType attributeType);
    
    @Override
    double base(@NotNull AttributeType attributeType);
    
    @Override
    void set(@NotNull AttributeType attributeType, double value);
    
    @Override
    void addModifier(@NotNull AttributeModifier attributeModifier);
    
    @Override
    boolean removeModifiers(@NotNull Predicate<AttributeModifier> filter);
    
    @Override
    @NotNull
    List<? extends AttributeModifier> getModifiers();
    
    @NotNull
    @Override
    Optional<AttributeModifier> getModifier(@NotNull Key key);
    
    @NotNull
    @Override
    <M extends AttributeModifier> Optional<M> getModifier(@NotNull Class<M> modifierClass);
    
    static AttributesInstanceSnapshot snapshot(@Nullable HariantEntity entity) {
        return entity != null ? new AttributesInstanceSnapshotImpl(entity) : AttributesInstanceSnapshotEnvironment.INSTANCE;
    }
    
}
