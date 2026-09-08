package me.hapyl.hariant.event;

import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstanceSnapshot;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.entity.damage.DamageInstance;
import me.hapyl.hariant.event.effect.HariantEffectEvent;
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
 * doing so will <b>not</b> trigger any entity-attribute related updates, nor will it call the {@link HariantEffectEvent} and will stack with
 * any other modifications, as the modifiers do.
 * </p>
 *
 * <p>
 * You can of course simply use {@link AttributesInstanceSnapshot#add(AttributeType, double)} do achieve the same outcome, but it's not advised.
 * </p>
 *
 * @see AttributesInstanceSnapshot
 */
public class HariantDamageCalculationsEvent extends AbstractHariantDamageEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final AttributesInstanceSnapshot snapshotEntity;
    private final AttributesInstanceSnapshot snapshotAttacker;
    
    public HariantDamageCalculationsEvent(@NotNull DamageInstance damageInstance, @NotNull AttributesInstanceSnapshot snapshotEntity, @NotNull AttributesInstanceSnapshot snapshotAttacker) {
        super(damageInstance);
        this.snapshotEntity = snapshotEntity;
        this.snapshotAttacker = snapshotAttacker;
    }
    
    public @NotNull AttributesInstanceSnapshot getSnapshotEntity() {
        return snapshotEntity;
    }
    
    public @NotNull AttributesInstanceSnapshot getSnapshotAttacker() {
        return snapshotAttacker;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
}
