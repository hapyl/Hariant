package me.hapyl.hariant.talent.field;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public final class DisplayFieldInstance implements ComponentLike {
    
    private final Component fieldName;
    private final Component fieldValue;
    
    private final Component component;
    
    public DisplayFieldInstance(@NotNull Component fieldName, @NotNull Component fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.component = Component.empty()
                                  .append(Component.text(" "))
                                  .append(fieldName.color(NamedTextColor.WHITE))
                                  .append(Component.text(" "))
                                  .append(fieldValue.color(NamedTextColor.GRAY));
    }
    
    @NotNull
    public Component getFieldName() {
        return fieldName;
    }
    
    @NotNull
    public Component getFieldValue() {
        return fieldValue;
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        return component;
    }
}
