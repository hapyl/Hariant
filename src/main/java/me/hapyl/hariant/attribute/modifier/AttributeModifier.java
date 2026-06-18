package me.hapyl.hariant.attribute.modifier;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.eterna.module.util.Streamable;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.TickingEntity;
import me.hapyl.hariant.entity.damage.AssistSource;
import me.hapyl.hariant.entity.effect.Effect;
import me.hapyl.hariant.entity.effect.EffectType;
import me.hapyl.hariant.ui.ComponentDisplay;
import me.hapyl.hariant.ui.ComponentDisplayable;
import me.hapyl.hariant.util.TickDuration;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class AttributeModifier
        implements
        Effect, TickDuration, TickingEntity, Streamable<AttributeModifier.Entry>,
        AttributeModifierAdder, ComponentDisplayable, Named, AssistSource {
    
    protected final Set<Entry> entries;
    protected final int duration;
    
    private final Key key;
    private final Component name;
    private final HariantEntity applier;
    
    private int tick;
    
    public AttributeModifier(@NotNull Key key, @NotNull Component name, @NotNull HariantEntity applier, int duration) {
        this.key = key;
        this.name = name;
        this.applier = applier;
        this.duration = duration;
        this.tick = duration;
        this.entries = Sets.newLinkedHashSet();
    }
    
    public <K extends Keyed & Named> AttributeModifier(@NotNull K k, @NotNull HariantEntity applier, int duration) {
        this(k.getKey(), k.getName(), applier, duration);
    }
    
    @NotNull
    @Override
    public HariantEntity source() {
        return applier;
    }
    
    @Override
    @NotNull
    public final Component getName() {
        return name;
    }
    
    @NotNull
    @Override
    public final Key getKey() {
        return key;
    }
    
    /**
     * @apiNote Realistically, a single {@link AttributeModifier} should not both buff and debuff at the same time, but since it
     * supports multiple entries, there isn't a good way to determine whether the modifier is a buff or a debuff.
     *
     * <p>
     * Thus, this method returns the first entry value, which determine by the value being positive or negative, or {@link EffectType#NEUTRAL}
     * if the modifier has no entries.
     * </p>
     *
     * <p>
     * It is recommended to override this method for unique modifiers and simply returns the effect type.
     * </p>
     */
    @NotNull
    @Override
    public EffectType getEffectType() {
        for (Entry entry : entries) {
            if (entry.isBuff()) {
                return EffectType.BUFF;
            }
            else {
                return EffectType.DEBUFF;
            }
        }
        
        return EffectType.NEUTRAL;
    }
    
    @Override
    public void onApply(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int duration) {
    }
    
    @Override
    public void onRemove(@NotNull HariantEntity entity, @NotNull HariantEntity applier) {
    }
    
    @Override
    public void onTick(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int tick) {
    }
    
    @NotNull
    public HariantEntity getApplier() {
        return applier;
    }
    
    @Override
    @NotNull
    public Stream<Entry> stream() {
        return entries.stream();
    }
    
    @Override
    public final void tick(@NotNull HariantEntity entity) {
        if (this.duration != HariantConstants.INDEFINITE_DURATION) {
            this.tick--;
        }
        
        this.onTick(entity, applier, tick);
    }
    
    @Override
    public int currentTick() {
        return tick;
    }
    
    @Override
    public int duration() {
        return duration;
    }
    
    @Override
    public boolean isOver() {
        return duration != HariantConstants.INDEFINITE_DURATION && tick <= 0;
    }
    
    @Override
    public AttributeModifierAdder of(@NotNull AttributeType attributeType, @NotNull AttributeModifierType modifierType, double value) {
        this.entries.add(entry(attributeType, modifierType, value));
        return this;
    }
    
    @Override
    public AttributeModifierAdder ofElementalDamageBonus(@NotNull AttributeModifierType modifierType, double value) {
        for (AttributeType attributeType : AttributeType.getElementalDamageBonuses()) {
            this.entries.add(entry(attributeType, modifierType, value));
        }
        
        return this;
    }
    
    @Override
    public AttributeModifierAdder ofElementalResistance(@NotNull AttributeModifierType modifierType, double value) {
        for (AttributeType attributeType : AttributeType.getElementalResistances()) {
            this.entries.add(entry(attributeType, modifierType, value));
        }
        
        return this;
    }
    
    @Override
    public void display(@NotNull Location location) {
        entries.forEach(entry -> {
            if (entry.isBuff()) {
                ComponentDisplay.ofAttributeBuff(entry.attributeType, location);
            }
            else {
                ComponentDisplay.ofAttributeDebuff(entry.attributeType, location);
            }
        });
    }
    
    @Override
    public String toString() {
        return "%s{key=%s, applier=%s, entries=%s}".formatted(this.getClass().getSimpleName(), this.key.getKey(), this.applier, this.entries);
    }
    
    @ApiStatus.Internal
    public final void onRemove0(@NotNull HariantEntity entity) {
        this.onRemove(entity, applier);
    }
    
    @NotNull
    public static Entry entry(@NotNull AttributeType attributeType, @NotNull AttributeModifierType modifierType, double value) {
        return new Entry(attributeType, modifierType, value);
    }
    
    public static final class Entry {
        
        private final AttributeType attributeType;
        private final AttributeModifierType modifierType;
        private final double value;
        
        Entry(@NotNull AttributeType attributeType, @NotNull AttributeModifierType modifierType, double value) {
            this.attributeType = attributeType;
            this.modifierType = modifierType;
            this.value = value;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.attributeType, this.modifierType);
        }
        
        @Override
        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }
            
            if (object == null || object.getClass() != this.getClass()) {
                return false;
            }
            
            final Entry that = (Entry) object;
            return Objects.equals(this.attributeType, that.attributeType) && Objects.equals(this.modifierType, that.modifierType);
        }
        
        @Override
        public String toString() {
            return "{attributeType=%s, modifierType=%s, value=%s}".formatted(attributeType, modifierType, value);
        }
        
        @NotNull
        public AttributeType attributeType() {
            return attributeType;
        }
        
        @NotNull
        public AttributeModifierType modifierType() {
            return modifierType;
        }
        
        public double value() {
            return value;
        }
        
        // All attributes are designed in a way when the higher the value, is better it is, so we
        // can just check for the value > 0 since it works for us, even if it's not a flat modifier
        public boolean isBuff() {
            return value > 0;
        }
        
        public boolean isDebuff() {
            return value < 0;
        }
    }
    
}
