package me.hapyl.hariant.element;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.element.anomaly.ElementalAnomaly;
import me.hapyl.hariant.element.anomaly.EnumAnomaly;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ElementAether extends ElementImpl {
    ElementAether() {
        super(Key.ofString("aether"), Component.text("✨"), Component.text("Æther"), Colors.ELEMENT_AETHER);
    }
    
    @NotNull
    @Override
    public ElementalAnomaly getElementalAnomaly() {
        return EnumAnomaly.INTANGIBILITY;
    }
}
