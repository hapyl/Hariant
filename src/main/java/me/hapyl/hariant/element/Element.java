package me.hapyl.hariant.element;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.component.Styled;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.hariant.element.anomaly.ElementalAnomaly;
import me.hapyl.hariant.util.Prefixed;
import me.hapyl.hariant.util.decimal.DecimalFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

public interface Element extends Keyed, Prefixed, Named, Styled, ComponentLike, DecimalFormat {
    
    @Override
    @NotNull
    Key getKey();
    
    @NotNull
    @Override
    Component getPrefix();
    
    @NotNull
    @Override
    Component getName();
    
    @Override
    @NotNull
    Style getStyle();
    
    @NotNull
    @Override
    Component format(double value);
    
    @NotNull
    ElementalAnomaly getElementalAnomaly();
    
    @NotNull
    @Override
    default Component asComponent() {
        final Style style = this.getStyle();
        
        return Component.empty().append(this.getPrefix().style(style)).appendSpace().append(this.getName().style(style));
    }
    
    @NotNull
    default Component asComponentDamage() {
        return asComponent().append(Component.text(" DMG", this.getStyle()));
    }
    
}
