package me.hapyl.hariant.entity;

import me.hapyl.eterna.module.component.Components;
import me.hapyl.eterna.module.text.SmallCaps;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface SmallCapsLike {
    
    @NotNull Component asSmallCaps();
    
    static @NotNull Component asSmallCaps(@NotNull String string) {
        return Component.text(SmallCaps.format(string));
    }
    
    static @NotNull Component asSmallCaps(@NotNull Component component) {
        return asSmallCaps(Components.toString(component));
    }
    
}
