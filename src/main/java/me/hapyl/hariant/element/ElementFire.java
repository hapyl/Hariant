package me.hapyl.hariant.element;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ElementFire extends ElementImpl {
    public ElementFire() {
        super(Key.ofString("fire"), Component.text("\uD83D\uDD25"), Component.text("Fire"), Colors.ELEMENT_FIRE);
    }
    
    @Override
    public @NotNull ElementalAnomalyType getElementalAnomaly() {
        return ElementalAnomalyType.BURN;
    }
    
    @Override
    public @NotNull AttributeType getOffensiveAttribute() {
        return AttributeType.FIRE_DAMAGE_BONUS;
    }
    
    @Override
    public @Nullable AttributeType getDefensiveAttribute() {
        return AttributeType.FIRE_RESISTANCE;
    }
    
}