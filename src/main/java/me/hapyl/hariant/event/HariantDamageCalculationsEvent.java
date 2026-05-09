package me.hapyl.hariant.event;

import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstanceSnapshot;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.entity.damage.DamageSource;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a damage calculations event, which is called right before the calculations are done.
 *
 * <p>
 * The event exposes the snapshot attributes of the entity and the attacker, allowing modifying them for specific conditions, such as increasing
 * a certain attribute for certain damage type, etc.
 * </p>
 *
 * <p>
 * It is recommended to use {@link AttributeModifier} via {@link AttributesInstanceSnapshot#addModifier(AttributeModifier)} to do so, such as
 * doing so will <b>not</b> trigger any entity-attribute related updates, nor will it call the {@link HariantAttributeEvent} and will stack with
 * any other modifications, as the modifiers do.
 * </p>
 *
 * <p>
 * You can of course simply use {@link AttributesInstanceSnapshot#add(AttributeType, double)} do achieve the same outcome, but it's not advised.
 * </p>
 *
 * @see AttributesInstanceSnapshot
 */
public class HariantDamageCalculationsEvent extends HariantEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final DamageSource damageSource;
    
    private final AttributesInstanceSnapshot snapshotEntity;
    private final AttributesInstanceSnapshot snapshotAttacker;
    
    public HariantDamageCalculationsEvent(@NotNull DamageSource damageSource, @NotNull AttributesInstanceSnapshot snapshotEntity, @NotNull AttributesInstanceSnapshot snapshotAttacker) {
        this.damageSource = damageSource;
        this.snapshotEntity = snapshotEntity;
        this.snapshotAttacker = snapshotAttacker;
    }
    
    @NotNull
    public DamageSource getDamageSource() {
        return damageSource;
    }
    
    @NotNull
    public AttributesInstanceSnapshot getSnapshotEntity() {
        return snapshotEntity;
    }
    
    @NotNull
    public AttributesInstanceSnapshot getSnapshotAttacker() {
        return snapshotAttacker;
    }
    
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
}
