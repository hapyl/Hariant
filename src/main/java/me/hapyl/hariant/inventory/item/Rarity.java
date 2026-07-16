package me.hapyl.hariant.inventory.item;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.component.Styled;
import me.hapyl.hariant.Colors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

public enum Rarity implements Named, Styled, ComponentLike {
    
    ONE_STAR(Component.text("⭐"), Style.style(Colors.RARITY_COMMON)),
    TWO_STAR(Component.text("⭐⭐"), Style.style(Colors.RARITY_UNCOMMON)),
    THREE_STAR(Component.text("⭐⭐⭐"), Style.style(Colors.RARITY_RARE)),
    FOUR_STAR(Component.text("⭐⭐⭐⭐"), Style.style(Colors.RARITY_EPIC)),
    FIVE_STAR(Component.text("⭐⭐⭐⭐⭐"), Style.style(Colors.RARITY_LEGENDARY)),
    SIX_STAR(Component.text("⭐⭐⭐⭐⭐⭐"), Style.style(Colors.RARITY_MYTHIC));
    
    private final Component name;
    private final Style style;
    private final Component component;
    
    Rarity(@NotNull Component name, @NotNull Style style) {
        this.name = name;
        this.style = style;
        this.component = name.style(style);
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public @NotNull Style getStyle() {
        return style;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return component;
    }
    
    public @NotNull Component asComponent(@NotNull Component component) {
        return Component.empty()
                        .append(this.component)
                        .appendSpace()
                        .append(component.style(style));
    }
    
}