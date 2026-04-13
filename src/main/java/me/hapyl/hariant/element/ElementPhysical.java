package me.hapyl.hariant.element;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.element.anomaly.ElementalAnomaly;
import me.hapyl.hariant.element.anomaly.EnumAnomaly;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class ElementPhysical extends ElementImpl implements Listener {
    
    ElementPhysical() {
        super(Key.ofString("physical"), Component.text("⚔"), Component.text("Physical"), Colors.ELEMENT_PHYSICAL);
    }
    
    @Override
    public @NotNull ElementalAnomaly getElementalAnomaly() {
        return EnumAnomaly.BLEED;
    }
}
