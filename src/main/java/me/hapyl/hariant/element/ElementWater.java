package me.hapyl.hariant.element;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.element.anomaly.ElementalAnomaly;
import me.hapyl.hariant.element.anomaly.EnumAnomaly;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ElementWater extends ElementImpl {
    ElementWater() {
        super(Key.ofString("water"), Component.text("\uD83C\uDF0A"), Component.text("Water"), Colors.ELEMENT_WATER);
    }
    
    @NotNull
    @Override
    public ElementalAnomaly getElementalAnomaly() {
        return EnumAnomaly.SOAKED;
    }
}
