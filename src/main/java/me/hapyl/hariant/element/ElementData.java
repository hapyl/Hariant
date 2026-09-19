package me.hapyl.hariant.element;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import me.hapyl.eterna.module.component.ProgressBar;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.anomaly.ElementalAnomaly;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyInstance;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.event.HariantElementalAnomalyEvent;
import me.hapyl.hariant.event.HariantElementalAnomalyQueueEvent;
import me.hapyl.hariant.event.HariantElementalApplicationEvent;
import me.hapyl.hariant.util.Resettable;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Queue;

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
    
    private static final NavigableMap<Integer, ? extends Component> QUEUE_FANCY_NUMBERS = ImmutableSortedMap.of(
            0, Component.text("⓪"),
            1, Component.text("①"),
            2, Component.text("②"),
            3, Component.text("③"),
            4, Component.text("④"),
            5, Component.text("⑤"),
            6, Component.text("⑥"),
            7, Component.text("⑦"),
            8, Component.text("⑧"),
            9, Component.text("⑨")
    );
    
    private final HariantEntity entity;
    private final Map<ElementType, ElementInstance> elementalMap;
    
    private @Nullable ElementType lastAppliedElement;
    private @Nullable ElementalAnomalyType lastTriggeredAnomaly;
    
    public ElementData(@NotNull HariantEntity entity) {
        this.entity = entity;
        this.elementalMap = Maps.newEnumMap(ElementType.class);
    }
    
    @Override
    public boolean applyElement(@NotNull ElementSource elementSource) {
        final double elementUnits = elementSource.getElementUnits();
        
        if (elementUnits <= 0) {
            return false;
        }
        
        final ElementType elementType = elementSource.getElementType();
        final HariantEntity source = elementSource.getSource();
        
        // Scale units by the source's Elemental Mastery and potency
        double units = this.calculateElementBuildUp(elementType, elementUnits, source);
        
        // Call elemental application event
        final HariantElementalApplicationEvent elementalApplicationEvent = new HariantElementalApplicationEvent(entity, elementType, units, source);
        
        if (elementalApplicationEvent.callEvent()) {
            return false;
        }
        
        // Reassign units from the event; cannot be 0
        units = elementalApplicationEvent.getElementalUnits();
        
        // Reassign last applied element
        this.lastAppliedElement = elementType;
        
        // Increment units
        final ElementInstance elementInstance = getElementInstance0(elementType);
        
        elementInstance.elementUnits += units;
        
        // Trigger anomaly
        if (elementInstance.elementUnits >= HariantConstants.ANOMALY_THRESHOLD) {
            final boolean wasTriggered = this.triggerAnomaly(elementSource, false);
            
            // Reset elemental units either way
            elementInstance.elementUnits = 0;
            
            return wasTriggered;
        }
        
        return false;
    }
    
    @Override
    public boolean triggerAnomaly(@NotNull ElementalAnomalySource anomalySource, boolean force) {
        final ElementalAnomalyType elementalAnomaly = anomalySource.getElementalAnomaly();
        final HariantEntity source = anomalySource.getSource();
        
        // Create anomaly instance
        final ElementInstance elementInstance = getElementInstance0(elementalAnomaly.getElementType());
        final ElementalAnomalyInstance elementalAnomalyInstance = elementalAnomaly.newInstance(anomalySource, entity, source);
        
        // If `force`, trigger the anomaly right away without decrementing the potency
        if (force) {
            elementInstance.triggerAnomaly(elementalAnomalyInstance, false);
        }
        else {
            // Otherwise, if there is already an instance exists, queue the anomaly
            if (elementInstance.currentInstance != null) {
                elementInstance.queueAnomaly(elementalAnomalyInstance);
            }
            // Otherwise, trigger normally
            else {
                elementInstance.triggerAnomaly(elementalAnomalyInstance, true);
            }
        }
        
        // We always consider triggering anomaly a success, even if it was queued
        return true;
    }
    
    @Override
    public double getElementalUnit(@NotNull ElementType elementType) {
        return getElementInstance0(elementType).elementUnits;
    }
    
    @Override
    public @Nullable ElementType lastAppliedElement() {
        return lastAppliedElement;
    }
    
    @Override
    public @Nullable ElementalAnomalyType lastTriggeredAnomaly() {
        return lastTriggeredAnomaly;
    }
    
    @Override
    public boolean isElementalAnomalyActive(@NotNull ElementalAnomaly elementalAnomaly) {
        return getElementInstance0(elementalAnomaly.getElementType()).currentInstance != null;
    }
    
    @Override
    public boolean endElementalAnomaly(@NotNull ElementalAnomaly elementalAnomaly) {
        return getElementInstance0(elementalAnomaly.getElementType()).endAnomaly();
    }
    
    @Override
    public int getElementalAnomalyQueueLength(@NotNull ElementalAnomaly elementalAnomaly) {
        return getElementInstance0(elementalAnomaly.getElementType()).queuedInstances.size();
    }
    
    public @NotNull Component getProgressBar(@NotNull ElementType elementType) {
        final ElementInstance elementInstance = getElementInstance0(elementType);
        
        return Component.empty()
                        .append(elementType.getPrefix().style(elementType.getStyle()))
                        .appendSpace()
                        .append(PROGRESS_BARS.get(elementType).build(elementInstance.elementUnits, HariantConstants.ANOMALY_THRESHOLD))
                        .appendSpace()
                        .append(Component.text("%,.0f".formatted(elementInstance.elementUnits)))
                        .append(getQueueComponent(elementType, elementInstance.queuedInstances.size()))
                        .appendSpace()
                        .append(elementInstance.currentInstance != null ? Component.text(elementInstance.currentInstance.currentTick()) : Component.text("N/A", Colors.DARK_GRAY));
    }
    
    private static @NotNull Component getQueueComponent(@NotNull ElementType elementType, int queue) {
        final Map.Entry<Integer, ? extends Component> entry = QUEUE_FANCY_NUMBERS.floorEntry(queue);
        
        return entry != null ? Component.space().append(entry.getValue().style(elementType.getStyle())) : Component.empty();
    }
    
    @Override
    public void tick() {
        elementalMap.forEach((elementType, elementInstance) -> {
            // Decrement elemental units
            if (elementInstance.elementUnits > 0) {
                elementInstance.elementUnits = Math.max(0, elementInstance.elementUnits - HariantConstants.ELEMENTAL_UNITS_DECREMENT_PER_TICK);
                
                // Tick entity when at least one unit is on an entity
                elementType.tickEntity(entity);
            }
            
            // Tick elemental anomaly
            if (elementInstance.currentInstance != null) {
                elementInstance.currentInstance.tick();
                
                // If anomaly ended, end current anomaly and reschedule
                if (elementInstance.currentInstance.isOver()) {
                    elementInstance.endAnomaly();
                    elementInstance.rescheduleAnomaly();
                }
            }
        });
    }
    
    public double calculateElementBuildUp(@NotNull ElementType elementType, double units, @Nullable HariantEntity source) {
        final ElementInstance elementInstance = getElementInstance0(elementType);
        final double elementalMastery = source != null ? source.getAttributes().get(AttributeType.ELEMENTAL_MASTERY) : 0;
        
        return units * (1 + elementalMastery / 500) * elementInstance.potency;
    }
    
    @Override
    public void reset() {
        // End elemental anomalies before clearing the map
        elementalMap.values().forEach(ElementInstance::endAnomaly);
        elementalMap.clear();
        
        lastAppliedElement = null;
        lastTriggeredAnomaly = null;
    }
    
    private @NotNull ElementInstance getElementInstance0(@NotNull ElementType elementType) {
        return this.elementalMap.computeIfAbsent(elementType, _ -> new ElementInstance(this, elementType));
    }
    
    private static @NotNull Map.Entry<ElementType, ProgressBar> createProgressBar(@NotNull ElementType elementType) {
        return Map.entry(elementType, new ProgressBar("|", 20, elementType.getStyle()));
    }
    
    public static class ElementInstance {
        
        private final ElementData elementData;
        private final ElementalAnomalyType elementalAnomaly;
        
        private final Queue<ElementalAnomalyInstance> queuedInstances;
        
        private @Nullable ElementalAnomalyInstance currentInstance;
        
        private double elementUnits;
        private double potency;
        
        ElementInstance(@NotNull ElementData elementData, @NotNull ElementType elementType) {
            this.elementData = elementData;
            this.elementalAnomaly = elementType.getElementalAnomaly();
            this.queuedInstances = Queues.newArrayDeque();
            this.elementUnits = 0;
            this.potency = elementalAnomaly.getPotency().initialValue();
        }
        
        public void decrementPotency() {
            this.potency = Math.max(this.potency - elementalAnomaly.getPotency().decay(), HariantConstants.ABSOLUTE_MINIMUM_ELEMENTAL_POTENCY);
        }
        
        public boolean endAnomaly() {
            if (currentInstance == null) {
                return false;
            }
            
            currentInstance.onEnd();
            currentInstance = null;
            
            return true;
        }
        
        public void triggerAnomaly(@NotNull ElementalAnomalyInstance anomalyInstance, boolean decrementPotency) {
            // Call event & return if cancelled
            if (new HariantElementalAnomalyEvent(elementData.entity, anomalyInstance).callEvent()) {
                return;
            }
            
            // End current anomaly if it exists
            this.endAnomaly();
            
            this.currentInstance = anomalyInstance;
            this.currentInstance.onStart();
            
            // Decrement potency if required
            if (decrementPotency) {
                this.decrementPotency();
            }
            
            // Mark as last triggered anomaly
            this.elementData.lastTriggeredAnomaly = elementalAnomaly;
        }
        
        public void rescheduleAnomaly() {
            final ElementalAnomalyInstance nextAnomalyInstance = queuedInstances.poll();
            
            if (nextAnomalyInstance != null) {
                this.triggerAnomaly(nextAnomalyInstance, true);
            }
        }
        
        public void queueAnomaly(@NotNull ElementalAnomalyInstance elementalAnomalyInstance) {
            // Call event and return if cancelled
            if (new HariantElementalAnomalyQueueEvent(elementData.entity, elementalAnomalyInstance).callEvent()) {
                return;
            }
            
            this.queuedInstances.add(elementalAnomalyInstance);
        }
        
    }
    
}