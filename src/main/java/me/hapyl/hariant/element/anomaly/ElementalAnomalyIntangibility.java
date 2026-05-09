package me.hapyl.hariant.element.anomaly;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.util.ComponentFormatter;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.*;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.term.EnumTerm;
import me.hapyl.hariant.util.decimal.Decimal;
import me.hapyl.hariant.util.decimal.DecimalFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ElementalAnomalyIntangibility extends ElementalAnomalyImpl {
    
    private final Key attributeKey = Key.ofString("anomaly_intangibility");
    
    private final Decimal damagePercentOfMaxHealth = Decimal.ofPercentage(10);
    private final double damageAdditional = 44;
    
    @DisplayField private final ComponentFormatter damage = () -> Component.text("%s Max HP + %s".formatted(damagePercentOfMaxHealth.format(), damageAdditional));
    
    @DisplayField private final Decimal resistanceReduction = Decimal.ofValue(-40, DecimalFormat.PERCENTAGE);
    @DisplayField private final Decimal resistanceReductionDuration = Decimal.ofSeconds(10);
    
    private final DamageSourceIdentity damageIdentity = DamageSourceIdentity.create(
            this,
            DeathMessage.createWithDefaultKiller("{player} drifted from the plane of reality")
    );
    
    ElementalAnomalyIntangibility() {
        super(Key.ofString("intangibility"), Component.text("Intangibility"));
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Causes the affected entity to drift from the plane of reality, taking "))
                         .append(ElementType.AETHER.asComponentDamage())
                         .append(Component.text(" equal to "))
                         .append(damagePercentOfMaxHealth)
                         .append(Component.text(" of their "))
                         .append(AttributeType.MAX_HEALTH)
                         .append(Component.text(" and reduces "))
                         .append(EnumTerm.ALL_TYPE_RESISTANCE)
                         .append(Component.text(" by "))
                         .append(resistanceReduction)
                         .append(Component.text(" for "))
                         .append(resistanceReductionDuration)
                         .append(Component.text("."))
        );
    }
    
    public double calculateDamage(@NotNull HariantEntity entity) {
        final double maxHealth = entity.getMaxHealth();
        
        return damagePercentOfMaxHealth.doubleValue() * maxHealth + damageAdditional;
    }
    
    @Override
    public void trigger(@NotNull HariantEntity entity, @Nullable HariantEntity source) {
        // Deal damage
        final DamageResult damageResult = entity.damage(
                DamageSource.builder(damageIdentity, calculateDamage(entity))
                            .source(source)
                            .elementType(ElementType.AETHER)
                            .damageType(DamageType.ANOMALY)
                            .build()
        );
        
        // If the entity has died after taking the damage, don't add the modifier
        if (damageResult == DamageResult.DEAD) {
            return;
        }
        
        entity.getAttributes().addModifier(new ElementalAnomalyIntangibilityModifier(source != null ? source : entity));
    }
    
    class ElementalAnomalyIntangibilityModifier extends AttributeModifier {
        ElementalAnomalyIntangibilityModifier(@NotNull HariantEntity applier) {
            super(attributeKey, ElementalAnomalyIntangibility.this.getName(), applier, resistanceReductionDuration.intValue());
            
            this.ofElementalResistance(AttributeModifierType.FLAT, resistanceReduction.doubleValue());
        }
        
        @Override
        public void display(@NotNull Location location) {
        }
    }
}
