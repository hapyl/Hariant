package me.hapyl.hariant.element;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.element.anomaly.ElementalAnomaly;
import me.hapyl.hariant.entity.HariantEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

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
    
    @NotNull
    @Override
    public Key getKey() {
        return element.getKey();
    }
    
    @NotNull
    @Override
    public Component getPrefix() {
        return element.getPrefix();
    }
    
    @NotNull
    @Override
    public Component getName() {
        return element.getName();
    }
    
    @Override
    @NotNull
    public Style getStyle() {
        return element.getStyle();
    }
    
    @NotNull
    @Override
    public Component format(double value) {
        return element.format(value);
    }
    
    @NotNull
    @Override
    public ElementalAnomaly getElementalAnomaly() {
        return element.getElementalAnomaly();
    }
    
    @Override
    public void tickEntity(@NotNull HariantEntity entity) {
        element.tickEntity(entity);
    }
    
}
