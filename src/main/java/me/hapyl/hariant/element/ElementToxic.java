package me.hapyl.hariant.element;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import net.kyori.adventure.text.Component;

public final class ElementToxic extends ElementImpl {
    ElementToxic() {
        super(Key.ofString("toxic"), Component.text("☢"), Component.text("Toxic"), Colors.ELEMENT_TOXIC);
    }
}
