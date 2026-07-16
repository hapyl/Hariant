package me.hapyl.hariant.element;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.event.HariantElementalAnomalyEvent;
import me.hapyl.hariant.util.Resettable;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ElementData implements ElementHandler, Ticking, Resettable {
    
    private final HariantEntity entity;
    private final Map<ElementType, Double> elementUnits;
    
    public ElementData(@NotNull HariantEntity entity) {
        this.entity = entity;
        this.elementUnits = Maps.newEnumMap(ElementType.class);
    }
    
    @Override
    public void applyElement(@NotNull ElementSource elementSource) {
        final double elementUnits = elementSource.getElementUnits();
        
        if (elementUnits <= 0) {
            return;
        }
        
        final ElementType elementType = elementSource.getElementType();
        final HariantEntity source = elementSource.getSource();
        
        // Scale units by the source's Elemental Mastery
        double units = this.calculateElementBuildUp(elementUnits, source);
        
        // Apply units
        final double totalUnits = this.elementUnits.merge(elementType, units, Double::sum);
        
        // Check for anomaly
        if (totalUnits >= HariantConstants.ANOMALY_THRESHOLD) {
            this.triggerAnomaly(elementType.getElementalAnomaly(), source);
            
            // Reset units
            this.elementUnits.remove(elementType);
        }
    }
    
    @Override
    public double getElementalUnit(@NotNull ElementType elementType) {
        return elementUnits.computeIfAbsent(elementType, _ -> 0.0);
    }
    
    @Override
    public void triggerAnomaly(@NotNull ElementalAnomalyType elementalAnomaly, @Nullable HariantEntity source) {
        // Call event
        if (new HariantElementalAnomalyEvent(entity, elementalAnomaly, source).callEvent()) {
            return;
        }
        
        elementalAnomaly.trigger(entity, source);
        elementalAnomaly.display(entity.getMidpointLocation());
        
        // Fx
        entity.playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 2.0f);
    }
    
    @Override
    public void tick() {
        for (ElementType elementType : ElementType.values()) {
            elementUnits.computeIfPresent(elementType, (_elementType, _value) -> {
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
    }
    
}