package me.hapyl.hariant.element.anomaly;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.frozen.FrozenHandler;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.event.HariantDamageEvent;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ElementalAnomalyFrozen extends ElementalAnomalyImpl implements Listener {
    
    private final int frozenDuration = Tick.fromSeconds(6);
    
    private final Decimal damageMultiplier = Decimal.ofPercentage(200);
    
    ElementalAnomalyFrozen() {
        super(Key.ofString("frozen"), Component.text("Frozen"));
    }
    
    @NotNull
    @Override
    public Style getStyle() {
        return ElementType.ICE.getStyle();
    }
    
    @EventHandler
    public void handleHariantDamageEvent(HariantDamageEvent ev) {
        final HariantEntity entity = ev.getEntity();
        
        if (!entity.isFrozen()) {
            return;
        }
        
        ev.multiplyDamage(() -> "Frozen", damageMultiplier.doubleValue());
        
        // Fx
        entity.playWorldSound(Sound.BLOCK_GLASS_BREAK, 0.0f);
    }
    
    @Override
    public void trigger(@NotNull HariantEntity entity, @Nullable HariantEntity source) {
        final int duration = this.calculateFrozenDuration(source);
        
        entity.freeze(new FrozenHandler(entity, duration));
    }
    
    public int calculateFrozenDuration(@Nullable HariantEntity source) {
        if (source == null) {
            return frozenDuration;
        }
        
        final double elementalMastery = source.getAttributes().get(AttributeType.ELEMENTAL_MASTERY);
        
        return (int) (frozenDuration * (1 + (elementalMastery / (elementalMastery + 500))));
    }
    
}
