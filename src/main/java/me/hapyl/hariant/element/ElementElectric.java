package me.hapyl.hariant.element;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.element.anomaly.ElementalAnomaly;
import me.hapyl.hariant.element.anomaly.EnumAnomaly;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ElementElectric extends ElementImpl {
    ElementElectric() {
        super(Key.ofString("electric"), Component.text("⚡"), Component.text("Electric"), Colors.ELEMENT_ELECTRIC);
    }
    
    @NotNull
    @Override
    public ElementalAnomaly getElementalAnomaly() {
        return EnumAnomaly.SHOCK;
    }
}
