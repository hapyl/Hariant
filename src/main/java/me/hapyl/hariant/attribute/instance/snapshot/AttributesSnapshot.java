package me.hapyl.hariant.attribute.instance.snapshot;

import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesBase;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageInstance;
import me.hapyl.hariant.event.HariantDamageCalculationsEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Represents a <b>snapshot</b> of attributes, where the attribute values are "frozen".
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
 * which will be empty for environment damage, nor modifying the environment attributes will do anything.
 * </p>
 */
public interface AttributesSnapshot extends AttributesBase {
    
    @NotNull Optional<HariantEntity> entity();
    
    @Nullable HariantEntity entityOrNull();
    
    @Override
    double get(@NotNull AttributeType attributeType);
    
    @Override
    double base(@NotNull AttributeType attributeType);
    
    @Override
    void set(@NotNull AttributeType attributeType, double value);
    
    @Override
    void add(@NotNull AttributeType attributeType, double value);
    
    void addModifier(@NotNull AttributeModifierSnapshot attributeModifier);
    
    static @NotNull AttributesSnapshot snapshot(@Nullable HariantEntity entity) {
        return entity != null ? new AttributesSnapshotImpl(entity) : AttributesSnapshotEnvironment.INSTANCE;
    }
    
}
