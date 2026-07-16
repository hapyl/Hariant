package me.hapyl.hariant.element;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ElementWater extends ElementImpl {
    ElementWater() {
        super(Key.ofString("water"), Component.text("\uD83C\uDF0A"), Component.text("Water"), Colors.ELEMENT_WATER);
    }
    
    @Override
    public @NotNull ElementalAnomalyType getElementalAnomaly() {
        return ElementalAnomalyType.SOAKED;
    }
    
    @Override
    public @NotNull AttributeType getOffensiveAttribute() {
        return AttributeType.WATER_DAMAGE_BONUS;
    }
    
    @Override
    public @NotNull AttributeType getDefensiveAttribute() {
        return AttributeType.WATER_RESISTANCE;
    }
}
