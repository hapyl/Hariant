package me.hapyl.hariant.attribute.instance;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifiable;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageInstance;
import me.hapyl.hariant.event.HariantDamageCalculationsEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Represents a <b>snapshot</b> of entity attributes, where the attribute values are "frozen".
 *
 * <p>
 * The snapshot attributes are used for damage calculations in {@link DamageInstance}, and are disposed right
 * after the calculations are finished.
 * </p>
 *
 * <p>
 * The snapshots are exposed in a {@link HariantDamageCalculationsEvent}, allowing listening to it and modifying the
 * snapshot attributes where needed, without affecting actual entity attributes.
 * </p>
 *
 * <p>
 * Note that since damage can come from non-entity contact (fall damage, lava, etc.), the {@link #entity()} returns an optional,
 * which will be empty for environment damage, nor will modifying the environment attributes will do anything.
 * </p>
 */
public interface AttributesInstanceSnapshot extends AttributesBase, AttributeModifiable {
    
    @NotNull
    Optional<HariantEntity> entity();
    
    @Override
    double get(@NotNull AttributeType attributeType);
    
    @Override
    double base(@NotNull AttributeType attributeType);
    
    @Override
    void set(@NotNull AttributeType attributeType, double value);
    
    @Override
    void addModifier(@NotNull AttributeModifier attributeModifier);
    
    default void addModifier(@NotNull HariantEntity applier, @NotNull AttributeModifier.Entry... entries) {
        this.addModifier(new AttributeModifierSnapshot(applier, entries));
    }
    
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
