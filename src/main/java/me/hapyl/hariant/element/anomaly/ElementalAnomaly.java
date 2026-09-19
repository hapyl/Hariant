package me.hapyl.hariant.element.anomaly;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.component.Styled;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.element.ElementalAnomalySource;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.util.Prefixed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElementalAnomaly extends Keyed, Prefixed, Named, Described, Styled, ComponentLike {
    
    @Override
    @NotNull Key getKey();
    
    @NotNull ElementType getElementType();
    
    @Override
    @NotNull Component getPrefix();
    
    @Override
    default @NotNull Component getPrefixStyled() {
        return getPrefix().style(getStyle());
    }
    
    @Override
    @NotNull Component getName();
    
    @Override
    @NotNull Component getDescription();
    
    @Override
    @NotNull Style getStyle();
    
    @NotNull ElementalPotency getPotency();
    
    @NotNull ElementalAnomalyInstance newInstance(@NotNull ElementalAnomalySource anomalySource, @NotNull HariantEntity entity, @Nullable HariantEntity source);
    
}