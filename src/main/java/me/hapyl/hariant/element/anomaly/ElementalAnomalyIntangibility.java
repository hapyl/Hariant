package me.hapyl.hariant.element.anomaly;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.element.ElementalAnomalySource;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.entity.damage.component.DamageComponents;
import me.hapyl.hariant.term.EnumTerminology;
import me.hapyl.hariant.util.decimal.Decimal;
import me.hapyl.hariant.util.decimal.DecimalFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public final class ElementalAnomalyIntangibility extends ElementalAnomalyImpl {
    
    private final Key attributeKey = Key.ofString("anomaly_intangibility");
    
    private final AttributeScaling damage = AttributeScaling.create(AttributeType.MAX_HEALTH, 5, 35);
    
    private final Decimal resistanceReduction = Decimal.ofValue(40, DecimalFormat.PERCENTAGE);
    private final Decimal resistanceReductionDuration = Decimal.ofSeconds(10);
    
    private final int duration = 30;
    
    private final DamageSourceIdentity damageIdentity = DamageSourceIdentity.create(
            Key.ofString("intangibility_damage_source"),
            this.getName(),
            DeathMessage.createWithDefaultKiller("{player} drifted from the plane of reality")
    );
    
    ElementalAnomalyIntangibility() {
        super(Key.ofString("intangibility"), ElementType.AETHER, Component.text("Intangibility"), new ElementalPotency(0.8, 0.2));
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Causes the affected entity to drift from the plane of reality, taking "))
                         .append(ElementType.AETHER.asComponentDamage())
                         .append(Component.text(" equal to "))
                         .append(damage)
                         .append(Component.text(" and reduces "))
                         .append(EnumTerminology.ALL_TYPE_RESISTANCE)
                         .append(Component.text(" by "))
                         .append(resistanceReduction)
                         .append(Component.text(" for "))
                         .append(resistanceReductionDuration)
                         .append(Component.text("."))
        );
    }
    
    @Override
    public @NotNull ElementalAnomalyInstance newInstance(@NotNull ElementalAnomalySource anomalySource, @NotNull HariantEntity entity, @Nullable HariantEntity source) {
        return new ElementalAnomalyIntangibilityInstance(anomalySource, entity, source);
    }
    
    public class ElementalAnomalyIntangibilityInstance extends ElementalAnomalyInstance {
        
        ElementalAnomalyIntangibilityInstance(@NotNull ElementalAnomalySource anomalySource, @NotNull HariantEntity entity, @Nullable HariantEntity source) {
            super(anomalySource, entity, source, duration);
        }
        
        @Override
        public void onStart() {
            super.onStart();
            
            // Deal damage based on entity max hp
            if (entity.damage(new IntangibilityDamageSource(source, damage.getScaledValue(entity), anomalySource)) == DamageResult.DEAD) {
                return;
            }
            
            // If entity survived, apply modifier
            entity.getAttributes().addModifier(new ElementalAnomalyIntangibilityModifier(source != null ? source : entity));
        }
        
    }
    
    public class ElementalAnomalyIntangibilityModifier extends AttributeModifier {
        
        ElementalAnomalyIntangibilityModifier(@NotNull HariantEntity applier) {
            super(attributeKey, ElementalAnomalyIntangibility.this.getName(), applier, resistanceReductionDuration.intValue());
            
            this.ofElementalResistance(AttributeModifierType.FLAT, -resistanceReduction.doubleValue());
        }
        
        @Override
        public void display(@NotNull Location location) {
        }
        
    }
    
    public class IntangibilityDamageSource extends DamageSourceImpl {
        
        private final ElementalAnomalySource anomalySource;
        
        IntangibilityDamageSource(@Nullable HariantEntity source, double damage, @NotNull ElementalAnomalySource anomalySource) {
            super(damageIdentity, source, DamageType.ANOMALY, ElementType.AETHER, DamageComponents.ofAnomaly(), Set.of(), damage, 0);
            
            this.anomalySource = anomalySource;
        }
        
        public @NotNull ElementalAnomalySource getAnomalySource() {
            return anomalySource;
        }
        
    }
    
}