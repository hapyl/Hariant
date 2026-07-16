package me.hapyl.hariant.util.decimal;

import me.hapyl.hariant.element.Element;
import me.hapyl.hariant.element.ElementType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class DecimalElementalApplicationImpl extends DecimalImpl {
    DecimalElementalApplicationImpl(@NotNull ElementType elementType, double units) {
        super(units, decimalFormat(elementType));
    }
    
    @NotNull
    private static DecimalFormat decimalFormat(@NotNull ElementType elementType) {
        return value -> Component.text("%,.0f".formatted(value))
                                 .appendSpace()
                                 .append(Element.ELEMENT_PREFIX)
                                 .appendSpace()
                                 .append(elementType.getName());
    }
}
