package me.hapyl.hariant.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public final class ComponentProgress {
    
    private static final Style DEFAULT_STYLE_EMPTY = Style.style(NamedTextColor.DARK_GRAY);
    
    private ComponentProgress() {
    }
    
    @NotNull
    public static Component create(@NotNull String text, @NotNull Style styleFilled, @NotNull Style styleEmpty, @Range(from = 0, to = 1) double progress) {
        final TextComponent.Builder builder = Component.text();
        final int length = text.length();
        
        for (int i = 0; i < length; i++) {
            final char ch = text.charAt(i);
            
            builder.append(Component.text(ch, progress > (double) i / length ? styleFilled : styleEmpty));
        }
        
        return builder.build();
    }
    
    @NotNull
    public static Component create(@NotNull String text, @NotNull Style styleFilled, @Range(from = 0, to = 1) double progress) {
        return create(text, styleFilled, DEFAULT_STYLE_EMPTY, progress);
    }
    
}
