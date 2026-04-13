package me.hapyl.hariant.element;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.element.anomaly.ElementalAnomaly;
import me.hapyl.hariant.element.anomaly.EnumAnomaly;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ElementFire extends ElementImpl {
    public ElementFire() {
        super(Key.ofString("fire"), Component.text("\uD83D\uDD25"), Component.text("Fire"), Colors.ELEMENT_FIRE);
    }
    
    @NotNull
    @Override
    public ElementalAnomaly getElementalAnomaly() {
        return EnumAnomaly.BURN;
    }
}