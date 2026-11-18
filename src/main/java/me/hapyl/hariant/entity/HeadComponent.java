package me.hapyl.hariant.entity;

import me.hapyl.hariant.Colors;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface HeadComponent {
    
    @NotNull Component asHeadComponent();
    
    default @NotNull Component asHeadComponentInBrackets() {
        return Component.empty()
                        .append(Component.text("[", Colors.GRAY))
                        .append(this.asHeadComponent())
                        .append(Component.text("]", Colors.GRAY));
    }
    
}
