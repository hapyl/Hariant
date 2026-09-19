package me.hapyl.hariant.util;

import me.hapyl.hariant.util.field.DisplayField;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a class that may return a formatted value as a {@link Component}.
 *
 * @see DisplayField
 */
public interface ComponentFormatter extends ComponentLike {
    
    @NotNull Component format();
    
    default @NotNull Component asComponent() {
        return format();
    }
    
}