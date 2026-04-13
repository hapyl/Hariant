package me.hapyl.hariant.element.anomaly;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.AttributesInstance;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponent;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.util.TriState;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class ElementalAnomalyBurn extends ElementalAnomalyImpl {
    
    private final Key modifierKey = Key.ofString("burn");
    
    private final Decimal attackDecrease = Decimal.ofPercentage(20);
    
    private final int burnDuration = Tick.fromSeconds(5);
    private final int burnPeriod = Tick.fromSeconds(0.5f);
    
    private final double burnDamage = 20;
    
    private final DeathMessage deathMessage = DeathMessage.createWithDefaultKiller("{player} burnt to death");
    
    ElementalAnomalyBurn() {
        super(Key.ofString("burn"), Component.text("Burn"));
        
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
    
    @Override
    public void trigger(@NotNull HariantEntity entity, @Nullable HariantEntity source) {
        final int duration = this.calculateBurnDuration(source);
        final double damage = this.calculateBurnDamage(source);
        
        entity.getAttributes().addModifier(new ElementalAnomalyBurnAttributeModifier(source, duration, damage));
    }
    
    @NotNull
    @Override
    public Style getStyle() {
        return ElementType.FIRE.getStyle();
    }
    
    public int calculateBurnDuration(@Nullable HariantEntity source) {
        if (source == null) {
            return burnDuration;
        }
        
        final double elementalMastery = source.getAttributes().get(AttributeType.ELEMENTAL_MASTERY);
        
        return (int) (burnDuration * (1 + elementalMastery / 500));
    }
    
    public double calculateBurnDamage(@Nullable HariantEntity source) {
        if (source == null) {
            return burnDamage;
        }
        
        final AttributesInstance attributes = source.getAttributes();
        
        final double attack = attributes.get(AttributeType.ATTACK);
        final double elementalMastery = attributes.get(AttributeType.ELEMENTAL_MASTERY);
        
        return (burnDamage * (1 + attack / 1000 + elementalMastery / 500));
    }
    
    public class ElementalAnomalyBurnAttributeModifier extends AttributeModifier {
        
        private final DamageSource damageSource;
        
        ElementalAnomalyBurnAttributeModifier(@Nullable HariantEntity applier, int duration, double damage) {
            super(modifierKey, applier, duration);
            
            this.of(AttributeType.ATTACK, AttributeModifierType.MULTIPLICATIVE, -attackDecrease.doubleValue());
            
            this.damageSource = new ElementalAnomalyBurnDamageSource(applier, damage);
        }
        
        @Override
        public void onApply(@NotNull HariantEntity entity, @Nullable HariantEntity applier) {
            entity.playWorldSound(Sound.ITEM_FLINTANDSTEEL_USE, 0.0f);
        }
        
        @Override
        public void onRemove(@NotNull HariantEntity entity, @Nullable HariantEntity applier) {
        }
        
        @Override
        public void onTick(@NotNull HariantEntity entity, @Nullable HariantEntity applier, int tick) {
            if (tick % burnPeriod == 0) {
                entity.damage(damageSource);
            }
            
            // Fx
            entity.spawnWorldParticle(entity.getMidpointLocation(), Particle.FLAME, 1, 0.25, 0.25, 0.25, 0.075f);
        }
    }
    
    public class ElementalAnomalyBurnDamageSource extends DamageSourceImpl {
        ElementalAnomalyBurnDamageSource(@Nullable HariantEntity source, double damage) {
            super(
                    DamageSourceIdentity.create(ElementalAnomalyBurn.this, deathMessage),
                    source,
                    DamageType.ANOMALY,
                    ElementType.FIRE,
                    List.of(DamageComponent.elemental()),
                    List.of(),
                    damage,
                    0
            );
        }
    }
}
