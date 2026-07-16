package me.hapyl.hariant.entity.trap;

import me.hapyl.eterna.module.text.SmallCaps;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

public final class TrapName implements ComponentLike {
    
    private final String name;
    private final Style style;
    private final Component component;
    
    public TrapName(@NotNull String name, @NotNull Style style) {
        this.name = SmallCaps.format(name);
        this.style = style;
        this.component = Component.text(name);
    }
    
    public @NotNull String getName() {
        return name;
    }
    
    public @NotNull Style getStyle() {
        return style;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return component;
    }
    
}
