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
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ElementalAnomalySoaked extends ElementalAnomalyImpl {
    
    private final Key modifierKey = Key.ofString("soaked");
    
    private final Decimal maxHealthDecrease = Decimal.ofPercentage(10);
    private final Decimal movementSpeedDecrease = Decimal.ofPercentage(20);
    
    private final int soakedDuration = Tick.fromSeconds(8);
    
    ElementalAnomalySoaked() {
        super(Key.ofString("soaked"), ElementType.WATER, Component.text("Drown"), new ElementalPotency(1, 0.1));
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Causes the affected entity to drown, decreasing their "))
                         .append(AttributeType.MAX_HEALTH)
                         .append(Component.text(" by "))
                         .append(maxHealthDecrease)
                         .append(Component.text(" and "))
                         .append(AttributeType.MOVEMENT_SPEED)
                         .append(Component.text(" by "))
                         .append(movementSpeedDecrease)
                         .append(Component.text("."))
        );
    }
    
    @Override
    public @NotNull ElementalAnomalyInstance newInstance(@NotNull ElementalAnomalySource anomalySource, @NotNull HariantEntity entity, @Nullable HariantEntity source) {
        final int duration = this.calculateSoakedDuration(source);
        
        return new ElementalAnomalySoakedInstance(anomalySource, entity, source, duration);
    }
    
    public int calculateSoakedDuration(@Nullable Attributable attributable) {
        if (attributable == null) {
            return soakedDuration;
        }
        
        final Attributes attributes = attributable.getAttributes();
        
        final double maxHealth = attributes.get(AttributeType.MAX_HEALTH);
        final double elementalMastery = attributes.get(AttributeType.ELEMENTAL_MASTERY);
        
        return (int) (soakedDuration * (1 + (maxHealth / (maxHealth + 5000) + elementalMastery / 1000)));
    }
    
    public class ElementalAnomalySoakedInstance extends ElementalAnomalyInstance {
        
        private final AttributeModifier attributeModifier;
        
        ElementalAnomalySoakedInstance(@NotNull ElementalAnomalySource anomalySource, @NotNull HariantEntity entity, @Nullable HariantEntity source, int duration) {
            super(anomalySource, entity, source, duration);
            
            this.attributeModifier = new ElementalAnomalySoakedAttributeModifier(source != null ? source : entity, duration);
        }
        
        @Override
        public void tick() {
            super.tick();
            
            // Fx
            entity.spawnWorldParticle(entity.getMidpointLocation(), Particle.SPLASH, 2, 0.25f, 0.4f, 0.25f, 0.0f);
        }
        
        @Override
        public void onStart() {
            super.onStart();
            
            // Apply modifier
            entity.getAttributes().addModifier(attributeModifier);
            
            // Fx
            entity.playWorldSound(Sound.ITEM_BUCKET_FILL, 0.75f);
        }
        
        @Override
        public void onEnd() {
            super.onEnd();
            
            // Remove modifier
            entity.getAttributes().removeModifier(attributeModifier);
        }
    }
    
    public class ElementalAnomalySoakedAttributeModifier extends AttributeModifier {
        
        ElementalAnomalySoakedAttributeModifier(@NotNull HariantEntity applier, int duration) {
            super(modifierKey, ElementalAnomalySoaked.this.getName(), applier, duration);
            
            this.of(AttributeType.MAX_HEALTH, AttributeModifierType.ADDITIVE, -maxHealthDecrease.doubleValue());
            this.of(AttributeType.MOVEMENT_SPEED, AttributeModifierType.ADDITIVE, -movementSpeedDecrease.doubleValue());
        }
        
    }
}
