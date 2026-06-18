package me.hapyl.hariant.hero;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.component.Styled;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.util.Prefixed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

public enum Archetype implements Prefixed, Named, Described, Styled, ComponentLike {
    
    DAMAGE(
            Component.text("💢"),
            Component.text("Damage"),
            Component.text("Excels in outputting a high amount of damage."),
            Style.style(Colors.DARK_RED)
    ),
    
    STRATEGY(
            Component.text("💡"),
            Component.text("Strategist"),
            Component.text("Strategists rely on their quick thinking to win."),
            Style.style(Colors.YELLOW)
    ),
    
    SUPPORT(
            Component.text("🍀"),
            Component.text("Support"),
            Component.text("Provides buffs and healing."),
            Style.style(Colors.GREEN)
    ),
    
    HEXBANE(
            Component.text("🕷"),
            Component.text("Hexbane"),
            Component.text("Excels at debuffing and hindering enemies."),
            Style.style(Colors.ARCHETYPE_HEXBANE)
    ),
    
    ;
    
    private final Component prefix;
    private final Component name;
    private final Component description;
    private final Style style;
    
    private final Component component;
    
    Archetype(@NotNull Component prefix, @NotNull Component name, @NotNull Component description, @NotNull Style style) {
        this.prefix = prefix;
        this.name = name;
        this.description = description;
        this.style = style;
        this.component = Component.empty()
                                  .append(prefix.style(style))
                                  .appendSpace()
                                  .append(name.style(style));
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
    public Component getDescription() {
        return description;
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
    
}
