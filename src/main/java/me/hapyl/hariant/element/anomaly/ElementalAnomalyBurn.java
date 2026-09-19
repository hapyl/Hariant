package me.hapyl.hariant.element.anomaly;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.Attributable;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.element.ElementalAnomalySource;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.Liquid;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponents;
import me.hapyl.hariant.event.HariantElementalAnomalyEvent;
import me.hapyl.hariant.event.HariantEntityLiquidEvent;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public final class ElementalAnomalyBurn extends ElementalAnomalyImpl implements Listener {
    
    private final Key modifierKey = Key.ofString("burn");
    
    private final Decimal attackDecrease = Decimal.ofPercentage(20);
    
    private final int burnDuration = Tick.fromSeconds(3);
    private final int burnPeriod = Tick.fromSeconds(0.5f);
    
    private final double burnDamage = 14;
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.createOfNamed(
            this, Key.ofString("burning"),
            DeathMessage.createWithDefaultKiller("{player} burnt to death")
    );
    
    ElementalAnomalyBurn() {
        super(Key.ofString("burn"), ElementType.FIRE, Component.text("Burning"), new ElementalPotency(1.1, 0.15));
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Causes the affected entity to start burning, taking "))
                         .append(ElementType.FIRE.asComponentDamage())
                         .append(Component.text(" over time and reducing their "))
                         .append(AttributeType.ATTACK)
                         .append(Component.text(" by "))
                         .append(attackDecrease)
                         .append(Component.text("."))
        );
    }
    
    @EventHandler
    public void handleHariantEntityLiquidEvent(HariantEntityLiquidEvent ev) {
        final HariantEntity entity = ev.getEntity();
        
        // Only pass if new liquid is water and entity has modifier
        if (ev.getLiquid() != Liquid.WATER || !entity.getAttributes().hasModifier(modifierKey)) {
            return;
        }
        
        this.extinguish(entity);
    }
    
    @EventHandler
    public void handleHariantElementalAnomalyEvent(HariantElementalAnomalyEvent ev) {
        final ElementalAnomalyType elementalAnomaly = ev.getElementalAnomaly();
        
        if (elementalAnomaly != ElementalAnomalyType.SOAKED && elementalAnomaly != ElementalAnomalyType.FROZEN) {
            return;
        }
        
        // Extinguish BURN when SOAKED or FROZEN is triggered
        this.extinguish(ev.getEntity());
    }
    
    public void extinguish(@NotNull HariantEntity entity) {
        if (!entity.isElementalAnomalyActive(this)) {
            return;
        }
        
        entity.endElementalAnomaly(this);
        
        // Fx
        entity.playWorldSound(Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 0.75f);
        entity.spawnWorldParticle(entity.getMidpointLocation(), Particle.SMOKE, 10, 0.2, 0.4, 0.2, 0.15f);
    }
    
    @Override
    public @NotNull ElementalAnomalyInstance newInstance(@NotNull ElementalAnomalySource anomalySource, @NotNull HariantEntity entity, @Nullable HariantEntity source) {
        final int duration = this.calculateBurnDuration(source);
        final double damage = this.calculateBurnDamage(source);
        
        return new ElementalAnomalyBurnInstance(anomalySource, entity, source, duration, damage);
    }
    
    public int calculateBurnDuration(@Nullable Attributable attributable) {
        if (attributable == null) {
            return burnDuration;
        }
        
        final double elementalMastery = attributable.getAttributes().get(AttributeType.ELEMENTAL_MASTERY);
        
        return (int) (burnDuration * (1 + elementalMastery / 500));
    }
    
    public double calculateBurnDamage(@Nullable Attributable attributable) {
        if (attributable == null) {
            return burnDamage;
        }
        
        final Attributes attributes = attributable.getAttributes();
        
        final double attack = attributes.get(AttributeType.ATTACK);
        final double elementalMastery = attributes.get(AttributeType.ELEMENTAL_MASTERY);
        
        return (burnDamage * (1 + attack / 1000 + elementalMastery / 500));
    }
    
    public class ElementalAnomalyBurnInstance extends ElementalAnomalyInstance {
        
        private final DamageSource damageSource;
        private final AttributeModifier attributeModifier;
        
        ElementalAnomalyBurnInstance(@NotNull ElementalAnomalySource anomalySource, @NotNull HariantEntity entity, @Nullable HariantEntity source, int duration, double damage) {
            super(anomalySource, entity, source, duration);
            
            this.damageSource = new ElementalAnomalyBurnDamageSource(source != null ? source : entity, damage, anomalySource);
            this.attributeModifier = new ElementalAnomalyBurnAttributeModifier(source != null ? source : entity, duration);
        }
        
        @Override
        public void tick() {
            super.tick();
            
            if (currentTick() % burnPeriod == 0) {
                entity.damage(damageSource);
            }
            
            // Fx
            entity.spawnWorldParticle(entity.getMidpointLocation(), Particle.FLAME, 1, 0.25, 0.25, 0.25, 0.075f);
        }
        
        @Override
        public void onStart() {
            super.onStart();
            
            // Add modifier
            entity.getAttributes().addModifier(attributeModifier);
            
            // Fx
            entity.playWorldSound(Sound.ITEM_FLINTANDSTEEL_USE, 0.0f);
        }
        
        @Override
        public void onEnd() {
            super.onEnd();
            
            // Remove modifier
            entity.getAttributes().removeModifier(attributeModifier);
        }
    }
    
    public class ElementalAnomalyBurnAttributeModifier extends AttributeModifier {
        
        ElementalAnomalyBurnAttributeModifier(@NotNull HariantEntity applier, int duration) {
            super(modifierKey, ElementalAnomalyBurn.this.getName(), applier, duration);
            
            this.of(AttributeType.ATTACK, AttributeModifierType.MULTIPLICATIVE, -attackDecrease.doubleValue());
        }
        
    }
    
    public class ElementalAnomalyBurnDamageSource extends DamageSourceImpl {
        
        private final ElementalAnomalySource elementalAnomalySource;
        
        ElementalAnomalyBurnDamageSource(@NotNull HariantEntity source, double damage, @NotNull ElementalAnomalySource elementalAnomalySource) {
            super(damageSourceIdentity, source, DamageType.ANOMALY, ElementType.FIRE, DamageComponents.ofAnomaly(), Set.of(), damage, 0);
            
            this.elementalAnomalySource = elementalAnomalySource;
        }
        
        public @NotNull ElementalAnomalySource getAnomalySource() {
            return elementalAnomalySource;
        }
        
    }
    
}
