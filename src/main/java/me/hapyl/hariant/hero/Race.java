package me.hapyl.hariant.hero;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.component.Styled;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.util.Prefixed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public enum Race implements Prefixed, Named, Described, Styled, ComponentLike {
    
    HUMAN(
            Component.text("🧑"),
            Component.text("Human"),
            Component.text("An ordinary human being."),
            Colors.SKIN_COLOR_0
    ),
    
    DEMON(
            Component.text("\uD83D\uDC7F"),
            Component.text("Demon"),
            Component.text("A being straight from hell, always arrogant towards others."),
            Colors.HELL
    ),
    
    ELF(
            Component.text("\uD83E\uDDDD"),
            Component.text("Elf"),
            Component.text("A race of beings that originates from long before the Kingdom."),
            Colors.SKIN_COLOR_0
    ),
    
    SHARK(
            Component.text("\uD83E\uDD88"),
            Component.text("Shark"),
            Component.text("An apex predator."),
            Colors.SHARK
    ),
    
    ALIEN(
            Component.text("👽"),
            Component.text("Alien"),
            Component.text("A creation from beyong this world."),
            Colors.ALIEN
    ),
    
    ;
    
    private final Component prefix;
    private final Component name;
    private final Component description;
    private final Style style;
    private final Component component;
    
    Race(@NotNull Component prefix, @NotNull Component name, @NotNull Component description, @NotNull TextColor color) {
        this.prefix = prefix;
        this.name = name;
        this.description = description;
        this.style = Style.style(color);
        this.component = Component.empty().append(prefix.color(color)).appendSpace().append(name.color(color));
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
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public @NotNull Component getDescription() {
        return description;
    }
    
    @Override
    public @NotNull Style getStyle() {
        return style;
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        return component;
    }
    
}