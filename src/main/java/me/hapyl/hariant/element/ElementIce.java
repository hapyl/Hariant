package me.hapyl.hariant.element;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import net.kyori.adventure.text.Component;

public final class ElementIce extends ElementImpl {
    ElementIce() {
        super(Key.ofString("ice"), Component.text("❄"), Component.text("Ice"), Colors.ELEMENT_ICE);
    }
}