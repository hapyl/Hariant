package me.hapyl.hariant.element;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class ElementToxic extends ElementImpl {
    
    ElementToxic() {
        super(Key.ofString("toxic"), Component.text("☢"), Component.text("Toxic"), Colors.ELEMENT_TOXIC);
    }
    
    @Override
    public @NotNull ElementalAnomalyType getElementalAnomaly() {
        return ElementalAnomalyType.INFESTED;
    }
    
    @Override
    public @NotNull AttributeType getOffensiveAttribute() {
        return AttributeType.TOXIC_DAMAGE_BONUS;
    }
    
    @Override
    public @NotNull AttributeType getDefensiveAttribute() {
        return AttributeType.TOXIC_RESISTANCE;
    }
    
}
