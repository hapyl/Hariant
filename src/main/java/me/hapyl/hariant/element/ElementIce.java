package me.hapyl.hariant.element;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ElementIce extends ElementImpl {
    ElementIce() {
        super(Key.ofString("ice"), Component.text("❄"), Component.text("Ice"), Colors.ELEMENT_ICE);
    }
    
    @Override
    public @NotNull ElementalAnomalyType getElementalAnomaly() {
        return ElementalAnomalyType.FROZEN;
    }
    
    @Override
    public @NotNull AttributeType getOffensiveAttribute() {
        return AttributeType.ICE_DAMAGE_BONUS;
    }
    
    @Override
    public @NotNull AttributeType getDefensiveAttribute() {
        return AttributeType.ICE_RESISTANCE;
    }
    
}