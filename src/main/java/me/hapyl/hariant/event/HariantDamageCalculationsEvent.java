package me.hapyl.hariant.event;

import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstanceSnapshot;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.entity.damage.DamageSource;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.event.effect.HariantEffectEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

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
public class HariantDamageCalculationsEvent extends HariantEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final AttributesInstanceSnapshot snapshotEntity;
    private final AttributesInstanceSnapshot snapshotAttacker;
    
    private @NotNull DamageSource damageSource;
    
    public HariantDamageCalculationsEvent(@NotNull DamageSource damageSource, @NotNull AttributesInstanceSnapshot snapshotEntity, @NotNull AttributesInstanceSnapshot snapshotAttacker) {
        this.damageSource = damageSource;
        this.snapshotEntity = snapshotEntity;
        this.snapshotAttacker = snapshotAttacker;
    }
    
    /**
     * Gets the <b>current</b> {@link DamageSource} of the event.
     *
     * <p>
     * Note that event listeners may modify the damage source instance, therefore you should not do a {@code instanceof} check,
     * since it may fail if the damage source was modified, instead you should use {@link DamageSource#compareIdentity(DamageSourceIdentity)}.
     * </p>
     *
     * @return the current damage source of the event.
     */
    public @NotNull DamageSource getDamageSource() {
        return damageSource;
    }
    
    public void setDamageSource(@NotNull Consumer<? super DamageSource.Builder> setter) {
        final DamageSource.Builder builder = damageSource.toBuilder();
        setter.accept(builder);
        
        this.damageSource = builder.build();
    }
    
    @NotNull
    public AttributesInstanceSnapshot getEntity() {
        return snapshotEntity;
    }
    
    @NotNull
    public AttributesInstanceSnapshot getAttacker() {
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
