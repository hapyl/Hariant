package me.hapyl.hariant.profile.ui;

import me.hapyl.hariant.Colors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

public class ActionbarBuilder implements ComponentLike {
    
    private static final Component SEPARATOR = Component.text(" × ", Colors.DARK_GRAY);
    
    private final TextComponent.Builder builder;
    private int length;
    
    public ActionbarBuilder() {
        this.builder = Component.text();
        this.length = 0;
    }
    
    public void append(@NotNull Component component) {
        if (length++ != 0) {
            builder.append(SEPARATOR);
        }
        
        builder.append(component);
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        return builder.build();
    }
    
}
