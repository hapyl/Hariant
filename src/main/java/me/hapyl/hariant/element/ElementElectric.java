package me.hapyl.hariant.element;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ElementElectric extends ElementImpl {
    
    ElementElectric() {
        super(Key.ofString("electric"), Component.text("⚡"), Component.text("Electric"), Colors.ELEMENT_ELECTRIC);
    }
    
    @Override
    public @NotNull ElementalAnomalyType getElementalAnomaly() {
        return ElementalAnomalyType.SHOCK;
    }
    
    @Override
    public @NotNull AttributeType getOffensiveAttribute() {
        return AttributeType.ELECTRIC_DAMAGE_BONUS;
    }
    
    @Override
    public @NotNull AttributeType getDefensiveAttribute() {
        return AttributeType.ELECTRIC_RESISTANCE;
    }
    
}
