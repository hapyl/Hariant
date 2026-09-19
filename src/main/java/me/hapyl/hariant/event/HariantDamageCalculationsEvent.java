package me.hapyl.hariant.event;

import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.snapshot.AttributesSnapshot;
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
 * It is recommended to use {@link AttributeModifier} via {@link AttributesSnapshot#addModifier(AttributeModifier)} to do so, such as
 * doing so will <b>not</b> trigger any entity-attribute related updates, nor will it call the {@link HariantEffectEvent} and will stack with
 * any other modifications, as the modifiers do.
 * </p>
 *
 * <p>
 * You can of course simply use {@link AttributesSnapshot#add(AttributeType, double)} do achieve the same outcome, but it's not advised.
 * </p>
 *
 * @see AttributesSnapshot
 */
public class HariantDamageCalculationsEvent extends AbstractHariantDamageEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final AttributesSnapshot snapshotEntity;
    private final AttributesSnapshot snapshotAttacker;
    
    public HariantDamageCalculationsEvent(@NotNull DamageInstance damageInstance, @NotNull AttributesSnapshot snapshotEntity, @NotNull AttributesSnapshot snapshotAttacker) {
        super(damageInstance);
        this.snapshotEntity = snapshotEntity;
        this.snapshotAttacker = snapshotAttacker;
    }
    
    public @NotNull AttributesSnapshot getEntitySnapshot() {
        return snapshotEntity;
    }
    
    public @NotNull AttributesSnapshot getAttackerSnapshot() {
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
