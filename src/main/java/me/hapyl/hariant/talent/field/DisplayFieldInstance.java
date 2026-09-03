package me.hapyl.hariant.talent.field;

import me.hapyl.hariant.Colors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public final class DisplayFieldInstance implements ComponentLike {
    
    private final Component fieldName;
    private final Component fieldValue;
    
    private final Component component;
    
    /**
     * {@see #create(Component, Component)}
     */
    DisplayFieldInstance(@NotNull Component fieldName, @NotNull Component fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.component = Component.empty()
                                  .append(Component.text(" "))
                                  .append(fieldName.color(Colors.WHITE))
                                  .append(Component.text(" "))
                                  .append(fieldValue.color(Colors.GRAY));
    }
    
    public @NotNull Component getFieldName() {
        return fieldName;
    }
    
    public @NotNull Component getFieldValue() {
        return fieldValue;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return component;
    }
    
    public static DisplayFieldInstance create(@NotNull Component fieldName, @NotNull Component fieldValue) {
        return new DisplayFieldInstance(fieldName, fieldValue);
    }
    
}
