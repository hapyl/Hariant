package me.hapyl.hariant.util;

import me.hapyl.hariant.talent.field.DisplayField;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

/**
 * @see DisplayField
 */
public interface ComponentFormatter extends ComponentLike {
    
    @NotNull
    Component format();
    
    default @NotNull Component asComponent() {
        return format();
    }
    
    static @NotNull ComponentFormatter format(@NotNull Component component) {
        return () -> component;
    }
    
}
