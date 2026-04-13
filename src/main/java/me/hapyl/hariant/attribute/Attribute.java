package me.hapyl.hariant.attribute;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.component.Styled;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.util.Prefixed;
import me.hapyl.hariant.util.decimal.DecimalFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

public interface Attribute extends Prefixed, Named, Described, Styled, ComponentLike, DecimalFormat {
    
    double defaultValue();
    
    double minValue();
    
    double maxValue();
    
    default double clamp(double value) {
        return Math.clamp(value, minValue(), maxValue());
    }
    
    boolean isBase();
    
    void update(@NotNull HariantEntity entity, final double newValue);
    
    @Override
    @NotNull
    Component getPrefix();
    
    @NotNull
    @Override
    default Component getPrefixStyled() {
        return this.getPrefix().style(this.getStyle());
    }
    
    @NotNull
    @Override
    Component getName();
    
    @NotNull
    @Override
    Component getDescription();
    
    @Override
    @NotNull
    Style getStyle();
    
    @NotNull
    Component abbreviation();
    
    @NotNull
    @Override
    Component format(double value);
    
    @NotNull
    @Override
    default Component asComponent() {
        final Style style = getStyle();
        
        return Component.empty().append(this.getPrefix().style(style)).appendSpace().append(this.getName().style(style));
    }
    
}
