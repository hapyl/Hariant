package me.hapyl.hariant.element;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.component.ProgressBar;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.anomaly.ElementalAnomaly;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.event.HariantElementalAnomalyEvent;
import me.hapyl.hariant.util.Resettable;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ElementData implements ElementHandler, Ticking, Resettable {
    
    private static final Map<ElementType, ProgressBar> PROGRESS_BARS = Map.ofEntries(
            createProgressBar(ElementType.PHYSICAL),
            createProgressBar(ElementType.FIRE),
            createProgressBar(ElementType.WATER),
            createProgressBar(ElementType.ICE),
            createProgressBar(ElementType.TOXIC),
            createProgressBar(ElementType.ELECTRIC),
            createProgressBar(ElementType.AETHER)
    );
    
    private final HariantEntity entity;
    private final Map<ElementType, Double> elementUnits;
    
    private ElementType lastAppliedElement;
    
    public ElementData(@NotNull HariantEntity entity) {
        this.entity = entity;
        this.elementUnits = Maps.newEnumMap(ElementType.class);
    }
    
    @Override
    public boolean applyElement(@NotNull ElementSource elementSource) {
        final double elementUnits = elementSource.getElementUnits();
        
        if (elementUnits <= 0) {
            return false;
        }
        
        final ElementType elementType = elementSource.getElementType();
        final HariantEntity source = elementSource.getSource();
        
        this.lastAppliedElement = elementType;
        
        // Scale units by the source's Elemental Mastery
        double units = this.calculateElementBuildUp(elementUnits, source);
        
        // Apply units
        final double totalUnits = this.elementUnits.merge(elementType, units, Double::sum);
        
        // Check for anomaly
        if (totalUnits >= HariantConstants.ANOMALY_THRESHOLD) {
            this.triggerAnomaly(elementSource);
            
            // Reset units
            this.elementUnits.remove(elementType);
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean triggerAnomaly(@NotNull ElementalAnomalySource anomalySource) {
        // Call event
        if (new HariantElementalAnomalyEvent(entity, anomalySource).callEvent()) {
            return false;
        }
        
        final ElementalAnomaly elementalAnomaly = anomalySource.getElementalAnomaly();
        final HariantEntity source = anomalySource.getSource();
        
        elementalAnomaly.trigger(entity, anomalySource);
        elementalAnomaly.display(entity.getMidpointLocation());
        
        if (source != null) {
            source.onElementalAnomaly(entity, anomalySource);
        }
        
        // Fx
        entity.playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 2.0f);
        return true;
    }
    
    @Override
    public double getElementalUnit(@NotNull ElementType elementType) {
        return elementUnits.computeIfAbsent(elementType, _ -> 0.0);
    }
    
    @Override
    public @Nullable ElementType lastAppliedElement() {
        return lastAppliedElement;
    }
    
    public @NotNull Component getProgressBar(@NotNull ElementType elementType) {
        final double elementalUnit = getElementalUnit(elementType);
        
        return Component.empty()
                        .append(elementType.getPrefix().style(elementType.getStyle()))
                        .appendSpace()
                        .append(PROGRESS_BARS.get(elementType).build(elementalUnit, HariantConstants.ANOMALY_THRESHOLD))
                        .appendSpace()
                        .append(Component.text("%,.0f".formatted(elementalUnit)));
    }
    
    @Override
    public void tick() {
        for (ElementType elementType : ElementType.values()) {
            elementUnits.computeIfPresent(elementType, (_, _value) -> {
                final double newValue = _value - HariantConstants.ELEMENTAL_UNITS_DECREMENT_PER_TICK;
                
                // If new value is higher than 0, it means that the element is currently present on an entity, call tick method
                if (newValue > 0) {
                    elementType.tickEntity(entity);
                }
                
                return newValue <= 0.0 ? null : newValue;
            });
        }
    }
    
    public double calculateElementBuildUp(double units, @Nullable HariantEntity source) {
        if (source == null) {
            return units;
        }
        
        final double elementalMastery = source.getAttributes().get(AttributeType.ELEMENTAL_MASTERY);
        
        return units * (1 + elementalMastery / 500);
    }
    
    @Override
    public void reset() {
        elementUnits.clear();
        lastAppliedElement = null;
    }
    
    private static @NotNull Map.Entry<ElementType, ProgressBar> createProgressBar(@NotNull ElementType elementType) {
        return Map.entry(elementType, new ProgressBar("|", 20, elementType.getStyle()));
    }
    
}