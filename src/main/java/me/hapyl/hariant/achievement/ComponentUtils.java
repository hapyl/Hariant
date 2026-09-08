package me.hapyl.hariant.achievement;

import me.hapyl.eterna.module.component.Components;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.util.ThisClassShouldNeMovedToEternaAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

@ThisClassShouldNeMovedToEternaAPI
public class ComponentUtils {
    
    public static final Style LORE_STYLE = Style.style(Colors.GRAY).decoration(TextDecoration.ITALIC, false);
    
    private static final Component SPARKLY_COMPONENT = Component.text("||");
    
    private ComponentUtils() {
    }
    
    public static @NotNull Component normalizeLore(@NotNull Component component) {
        return Components.normalizeStyle(component, LORE_STYLE);
    }
    
    public static @NotNull Component sparkly(@NotNull Component component, @NotNull Style sparkStyle) {
        final Component sparklyComponent = SPARKLY_COMPONENT.style(sparkStyle.decorate(TextDecoration.OBFUSCATED));
        
        return Component.empty()
                        .append(sparklyComponent)
                        .appendSpace()
                        .append(component)
                        .appendSpace()
                        .append(sparklyComponent);
    }
    
    // TODO (xanyjl @ Friday, September 4) -> Also add `styleWithoutItalic()` or something
    
}
