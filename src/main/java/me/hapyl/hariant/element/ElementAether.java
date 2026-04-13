package me.hapyl.hariant.element;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import net.kyori.adventure.text.Component;

public final class ElementAether extends ElementImpl {
    ElementAether() {
        super(Key.ofString("aether"), Component.text("✨"), Component.text("Æther"), Colors.ELEMENT_AETHER);
    }
    
}
