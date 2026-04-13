package me.hapyl.hariant.element;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import net.kyori.adventure.text.Component;

public final class ElementElectric extends ElementImpl {
    ElementElectric() {
        super(Key.ofString("electric"), Component.text("⚡"), Component.text("Electric"), Colors.ELEMENT_ELECTRIC);
    }
}
