package me.hapyl.hariant.hero;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.component.Styled;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.util.Prefixed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

public enum Definition implements Prefixed, Named, Styled, ComponentLike {
    
    ABYSSAL_CORROSION(
            Component.text("☣"),
            Component.text("Abyssal Corrosion"),
            Style.style(Colors.ABYSS)
    ),
    
    ALCHEMICAL_MADNESS(
            Component.text("\uD83E\uDD2F"),
            Component.text("Alchemical Madness"),
            Style.style(Colors.ELEMENT_TOXIC)
    ),
    
    ABYSSAL_CURSE(
            Component.text("\uD83D\uDDEF"),
            Component.text("Abyssal Curse"),
            Style.style(Colors.ABYSSAL_CURSE)
    ),
    
    SOUL_FRAGMENT(
            Component.text("✦"),
            Component.text("Soul Fragment"),
            Style.style(Colors.SOUL)
    ),
    
    ;
    
    private final Component prefix;
    private final Component name;
    private final Style style;
    
    private final Component component;
    
    Definition(@NotNull Component prefix, @NotNull Component name, @NotNull Style style) {
        this.prefix = prefix;
        this.name = name;
        this.style = style;
        this.component = prefix.style(style).appendSpace().append(name.style(style));
    }
    
    @NotNull
    @Override
    public Component getPrefix() {
        return prefix;
    }
    
    @NotNull
    @Override
    public Component getPrefixStyled() {
        return prefix.style(style);
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @NotNull
    @Override
    public Style getStyle() {
        return style;
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        return component;
    }
    
    @NotNull
    public Component prefix(@NotNull Component component) {
        return Component.empty()
                        .append(prefix.style(style))
                        .appendSpace()
                        .append(component.style(style));
    }
    
}
