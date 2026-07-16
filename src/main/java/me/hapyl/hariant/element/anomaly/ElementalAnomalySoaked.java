package me.hapyl.hariant.element.anomaly;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstance;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
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
        super(Key.ofString("soaked"), Component.text("Drown"), ElementType.WATER);
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Causes the affected entity to become soaked, decreasing their "))
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
    public void trigger(@NotNull HariantEntity entity, @Nullable HariantEntity source) {
        final int duration = this.calculateSoakedDuration(source);
        
        entity.getAttributes().addModifier(new ElementalAnomalySoakedAttributeModifier(source != null ? source : entity, duration));
    }
    
    @Override
    public boolean isAnomalyActive(@NotNull HariantEntity entity) {
        return entity.getAttributes().hasModifier(modifierKey);
    }
    
    public int calculateSoakedDuration(@Nullable HariantEntity source) {
        if (source == null) {
            return soakedDuration;
        }
        
        final AttributesInstance attributes = source.getAttributes();
        
        final double maxHealth = attributes.get(AttributeType.MAX_HEALTH);
        final double elementalMastery = attributes.get(AttributeType.ELEMENTAL_MASTERY);
        
        return (int) (soakedDuration * (1 + (maxHealth / (maxHealth + 5000) + elementalMastery / 1000)));
    }
    
    public class ElementalAnomalySoakedAttributeModifier extends AttributeModifier {
        ElementalAnomalySoakedAttributeModifier(@NotNull HariantEntity applier, int duration) {
            super(modifierKey, ElementalAnomalySoaked.this.getName(), applier, duration);
            
            this.of(AttributeType.MAX_HEALTH, AttributeModifierType.ADDITIVE, -maxHealthDecrease.doubleValue());
            this.of(AttributeType.MOVEMENT_SPEED, AttributeModifierType.ADDITIVE, -movementSpeedDecrease.doubleValue());
        }
        
        @Override
        public void onApply(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int duration) {
            entity.playWorldSound(Sound.ITEM_BUCKET_FILL, 0.75f);
        }
        
        @Override
        public void onTick(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int tick) {
            entity.spawnWorldParticle(entity.getMidpointLocation(), Particle.SPLASH, 2, 0.25f, 0.4f, 0.25f, 0.0f);
        }
    }
}
