package me.hapyl.hariant.element.anomaly;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.Element;
import me.hapyl.hariant.entity.HariantEntity;
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
    
    @DisplayField private final Decimal resistanceReduction = Decimal.ofValue(-40, DecimalFormat.PERCENTAGE);
    @DisplayField private final Decimal resistanceReductionDuration = Decimal.ofSeconds(10);
    
    ElementalAnomalyIntangibility() {
        super(Key.ofString("intangibility"), Component.text("Intangibility"));
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Causes the affected entity to drift from the plane of reality, reducing their "))
                         .append(EnumTerm.ALL_TYPE_RESISTANCE)
                         .append(Component.text(" by "))
                         .append(resistanceReduction)
                         .append(Component.text(" for "))
                         .append(resistanceReductionDuration)
                         .append(Component.text("."))
        );
    }
    
    @Override
    public void trigger(@NotNull HariantEntity entity, @Nullable HariantEntity source) {
        entity.getAttributes().addModifier(new ElementalAnomalyIntangibilityModifier(source));
    }
    
    class ElementalAnomalyIntangibilityModifier extends AttributeModifier {
        ElementalAnomalyIntangibilityModifier(@Nullable HariantEntity applier) {
            super(attributeKey, applier, resistanceReductionDuration.intValue());
            
            this.ofElementalResistance(AttributeModifierType.FLAT, resistanceReduction.doubleValue());
        }
        
        @Override
        public void display(@NotNull Location location) {
        }
    }
}
