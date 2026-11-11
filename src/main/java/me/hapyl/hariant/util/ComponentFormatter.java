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
    
    /**
     * Gets the formatted version of this object.
     *
     * <p>
     * The component is used directly for display field, thus must not be styled!
     * </p>
     *
     * @return the formatted object.
     */
    @NotNull Component format();
    
    @Override
    default @NotNull Component asComponent() {
        return this.format();
    }
    
}