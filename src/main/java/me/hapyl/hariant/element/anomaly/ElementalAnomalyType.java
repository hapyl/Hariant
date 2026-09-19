package me.hapyl.hariant.element.anomaly;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.element.ElementalAnomalySource;
import me.hapyl.hariant.entity.HariantEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ElementalAnomalyType implements ElementalAnomaly {
    
    BLEED(new ElementalAnomalyBleed()),
    BURN(new ElementalAnomalyBurn()),
    SOAKED(new ElementalAnomalySoaked()),
    FROZEN(new ElementalAnomalyFrozen()),
    INFESTED(new ElementalAnomalyInfested()),
    SHOCK(new ElementalAnomalyShock()),
    INTANGIBILITY(new ElementalAnomalyIntangibility());
    
    private final ElementalAnomaly anomaly;
    
    ElementalAnomalyType(@NotNull ElementalAnomaly anomaly) {
        this.anomaly = anomaly;
    }
    
    @Override
    public @NotNull Key getKey() {
        return anomaly.getKey();
    }
    
    @Override
    public @NotNull ElementType getElementType() {
        return anomaly.getElementType();
    }
    
    @Override
    public @NotNull Component getPrefix() {
        return anomaly.getPrefix();
    }
    
    @Override
    public @NotNull Component getPrefixStyled() {
        return anomaly.getPrefixStyled();
    }
    
    @Override
    public @NotNull Component getName() {
        return anomaly.getName();
    }
    
    @Override
    public @NotNull Component getDescription() {
        return anomaly.getDescription();
    }
    
    @Override
    public @NotNull Style getStyle() {
        return anomaly.getStyle();
    }
    
    @Override
    public @NotNull ElementalPotency getPotency() {
        return anomaly.getPotency();
    }
    
    @Override
    public @NotNull ElementalAnomalyInstance newInstance(@NotNull ElementalAnomalySource anomalySource, @NotNull HariantEntity entity, @Nullable HariantEntity source) {
        return anomaly.newInstance(anomalySource, entity, source);
    }
    
    @Override
    public @NotNull Component asComponent() {
        return anomaly.asComponent();
    }
}
