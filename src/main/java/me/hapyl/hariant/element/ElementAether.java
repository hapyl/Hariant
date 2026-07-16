package me.hapyl.hariant.element;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ElementAether extends ElementImpl {
    
    ElementAether() {
        super(Key.ofString("aether"), Component.text("✨"), Component.text("Æther"), Colors.ELEMENT_AETHER);
    }
    
    @Override
    public @NotNull ElementalAnomalyType getElementalAnomaly() {
        return ElementalAnomalyType.INTANGIBILITY;
    }
    
    @Override
    public @NotNull AttributeType getOffensiveAttribute() {
        return AttributeType.AETHER_DAMAGE_BONUS;
    }
    
    @Override
    public @NotNull AttributeType getDefensiveAttribute() {
        return AttributeType.AETHER_RESISTANCE;
    }
    
}
