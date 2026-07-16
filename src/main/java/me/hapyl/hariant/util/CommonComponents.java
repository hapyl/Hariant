package me.hapyl.hariant.util;

import me.hapyl.eterna.module.text.Capitalizable;
import me.hapyl.hariant.Colors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public final class CommonComponents implements ComponentLike {
    
    public static final CommonComponents ENEMY;
    public static final CommonComponents ALLY;
    public static final CommonComponents HEALTH;
    
    static {
        ENEMY = create("enemy", "enemies", Colors.RED);
        ALLY = create("ally", "allies", Colors.GREEN);
        HEALTH = create("health", "health", Colors.ATTRIBUTE_MAX_HEALTH);
    }
    
    private final String text;
    private final String textPlural;
    private final Style style;
    
    private CommonComponents(@NotNull String text, @NotNull String textPlural, @NotNull Style style) {
        this.text = text;
        this.textPlural = textPlural;
        this.style = style;
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        // Default to text since that's what we mostly need
        return this.text();
    }
    
    @NotNull
    public Component text() {
        return Component.text(text, style);
    }
    
    @NotNull
    public Component textCapitalized() {
        return Component.text(Capitalizable.capitalize(text), style);
    }
    
    @NotNull
    public Component textPlural() {
        return Component.text(textPlural, style);
    }
    
    @NotNull
    public Component textPluralCapitalized() {
        return Component.text(Capitalizable.capitalize(textPlural), style);
    }
    
    @NotNull
    private static CommonComponents create(@NotNull String text, @NotNull String textPlural, @NotNull Style style) {
        return new CommonComponents(text, textPlural, style);
    }
    
    @NotNull
    private static CommonComponents create(@NotNull String text, @NotNull String textPlural, @NotNull TextColor color) {
        return create(text, textPlural, Style.style(color));
    }
    
}
