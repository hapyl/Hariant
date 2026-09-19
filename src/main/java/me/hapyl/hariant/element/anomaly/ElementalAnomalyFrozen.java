package me.hapyl.hariant.element.anomaly;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.Attributable;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.element.ElementalAnomalySource;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.mutator.DamageMutator;
import me.hapyl.hariant.entity.trap.frozen.TrapFrozen;
import me.hapyl.hariant.event.HariantDamageComputeEvent;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ElementalAnomalyFrozen extends ElementalAnomalyImpl implements Listener {
    
    private final int frozenDuration = Tick.fromSeconds(5);
    
    private final Decimal damageMultiplier = Decimal.ofPercentage(200);
    
    ElementalAnomalyFrozen() {
        super(Key.ofString("frozen"), ElementType.ICE, Component.text("Frozen"), new ElementalPotency(0.9, 0.15));
        
        setDescription(
                Component.empty()
                         .append(Component.text("Causes the affected entity to freeze, unable to attack or move."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Frozen entities take "))
                         .append(Component.text("%sx".formatted(damageMultiplier.doubleValue()), Colors.RED))
                         .append(Component.text(" DMG from all sources."))
        );
    }
    
    @EventHandler
    public void handleHariantDamageComputeEvent(HariantDamageComputeEvent ev) {
        final HariantEntity entity = ev.getEntity();
        
        if (!(entity.getTrap() instanceof TrapFrozen)) {
            return;
        }
        
        ev.mutateDamage(() -> "Frozen", DamageMutator.multiply(), damageMultiplier);
        
        // Fx
        entity.playWorldSound(Sound.BLOCK_GLASS_BREAK, 0.0f);
    }
    
    @Override
    public @NotNull ElementalAnomalyInstance newInstance(@NotNull ElementalAnomalySource anomalySource, @NotNull HariantEntity entity, @Nullable HariantEntity source) {
        final int duration = this.calculateFrozenDuration(source);
        
        return new ElementalAnomalyFrozenInstance(anomalySource, entity, source, duration);
    }
    
    public int calculateFrozenDuration(@Nullable Attributable attributes) {
        if (attributes == null) {
            return frozenDuration;
        }
        
        final double elementalMastery = attributes.getAttributes().get(AttributeType.ELEMENTAL_MASTERY);
        
        return (int) (frozenDuration * (1 + (elementalMastery / (elementalMastery + 500))));
    }
    
    public class ElementalAnomalyFrozenInstance extends ElementalAnomalyInstance {
        
        ElementalAnomalyFrozenInstance(@NotNull ElementalAnomalySource anomalySource, @NotNull HariantEntity entity, @Nullable HariantEntity source, int duration) {
            super(anomalySource, entity, source, duration);
        }
        
        @Override
        public void onStart() {
            super.onStart();
            
            // Trap the entity
            entity.trap(new TrapFrozen(entity, source != null ? source : entity, duration()));
        }
        
    }
    
}
