package me.hapyl.hariant.hero;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.component.Styled;
import me.hapyl.hariant.util.Prefixed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public enum Gender implements Prefixed, Named, Styled, ComponentLike {
    
    MALE(
            Component.text("♂"),
            Component.text("Male"),
            NamedTextColor.AQUA
    ),
    
    FEMALE(
            Component.text("♀"),
            Component.text("Female"),
            NamedTextColor.LIGHT_PURPLE
    ),
    
    OTHER(
            Component.text("❓"),
            Component.text("Other"),
            NamedTextColor.GRAY
    );
    
    private final Component prefix;
    private final Component name;
    private final Style style;
    
    Gender(@NotNull Component prefix, @NotNull Component name, @NotNull TextColor textColor) {
        this.prefix = prefix;
        this.name = name;
        this.style = Style.style(textColor);
    }
    
    @NotNull
    @Override
    public Component getPrefix() {
        return prefix;
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
        return Component.empty()
                        .append(prefix.style(style))
                        .appendSpace()
                        .append(name.style(style));
    }
    
}
