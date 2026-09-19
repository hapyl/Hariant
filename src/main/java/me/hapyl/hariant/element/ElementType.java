package me.hapyl.hariant.element;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.element.anomaly.ElementalAnomalyType;
import me.hapyl.hariant.entity.HariantEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ElementType implements Element {
    
    PHYSICAL(new ElementPhysical()),
    FIRE(new ElementFire()),
    WATER(new ElementWater()),
    ICE(new ElementIce()),
    TOXIC(new ElementToxic()),
    ELECTRIC(new ElementElectric()),
    AETHER(new ElementAether());
    
    private final Element element;
    
    ElementType(@NotNull Element element) {
        this.element = element;
    }
    
    @Override
    public @NotNull Key getKey() {
        return element.getKey();
    }
    
    @Override
    public @NotNull Component getPrefix() {
        return element.getPrefix();
    }
    
    @Override
    public @NotNull Component getName() {
        return element.getName();
    }
    
    @Override
    public @NotNull Style getStyle() {
        return element.getStyle();
    }
    
    @Override
    public @NotNull Component format(double value) {
        return element.format(value);
    }
    
    @Override
    public @NotNull ElementalAnomalyType getElementalAnomaly() {
        return element.getElementalAnomaly();
    }
    
    @Override
    public @Nullable AttributeType getOffensiveAttribute() {
        return element.getOffensiveAttribute();
    }
    
    @Override
    public @Nullable AttributeType getDefensiveAttribute() {
        return element.getDefensiveAttribute();
    }
    
    @Override
    public void tickEntity(@NotNull HariantEntity entity) {
        element.tickEntity(entity);
    }
    
}
