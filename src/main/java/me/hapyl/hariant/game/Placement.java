package me.hapyl.hariant.game;

import me.hapyl.eterna.module.component.Styled;
import me.hapyl.hariant.entity.SmallCapsLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public enum Placement implements ComponentLike, SmallCapsLike, Styled {
    
    FIRST_PLACE(Component.text("1st Place"), Style.style(TextColor.color(0xFFD700))),
    SECOND_PLACE(Component.text("2nd Place"), Style.style(TextColor.color(0xC0C0C0))),
    THIRD_PLACE(Component.text("3rd Place"), Style.style(TextColor.color(0xB26924))),
    PARTICIPATION(Component.text("Participation"), Style.style(TextColor.color(0x607D8B)));
    
    private static final Placement[] VALUES = values();
    
    private final Component name;
    private final Component smallCaps;
    private final Style style;
    
    Placement(@NotNull Component name, @NotNull Style style) {
        this.name = name;
        this.smallCaps = SmallCapsLike.asSmallCaps(name);
        this.style = style;
    }
    
    public boolean isWinner() {
        return this == FIRST_PLACE;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return name;
    }
    
    @Override
    public @NotNull Component asSmallCaps() {
        return smallCaps;
    }
    
    @Override
    public @NotNull Style getStyle() {
        return style;
    }
    
    public static @NotNull Placement placement(int bracket) {
        return bracket < VALUES.length ? VALUES[bracket] : PARTICIPATION;
    }
    
}