package me.hapyl.hariant.element;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.element.anomaly.ElementalAnomaly;
import me.hapyl.hariant.element.anomaly.EnumAnomaly;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ElementToxic extends ElementImpl {
    ElementToxic() {
        super(Key.ofString("toxic"), Component.text("☢"), Component.text("Toxic"), Colors.ELEMENT_TOXIC);
    }
    
    @NotNull
    @Override
    public ElementalAnomaly getElementalAnomaly() {
        return EnumAnomaly.INFESTED;
    }
}
