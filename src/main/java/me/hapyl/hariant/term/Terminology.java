package me.hapyl.hariant.term;

import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.component.Named;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public interface Terminology extends Named, ComponentLike {
    
    @NotNull
    Style TERM_STYLE = Style.style(TextColor.color(0xFEFEFE), TextDecoration.UNDERLINED);
    
    @NotNull
    @Override
    Component getName();
    
    @NotNull
    Component explainTerm();
    
    @NotNull
    @Override
    default Component asComponent() {
        return getName().style(TERM_STYLE);
    }
    
    @NotNull
    default Component asComponentLowerCase() {
        return Component.text(Components.toString(getName()).toLowerCase()).style(TERM_STYLE);
    }
    
}
