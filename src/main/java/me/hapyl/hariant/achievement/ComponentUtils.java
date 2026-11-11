package me.hapyl.hariant.achievement;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public class ComponentUtils {
    
    private static final Component SPARKLY_COMPONENT = Component.text("||");
    
    private ComponentUtils() {
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
    
}
