package me.hapyl.hariant.util.decimal;

import me.hapyl.hariant.element.ElementType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class DecimalElementalApplicationImpl extends DecimalImpl {
    DecimalElementalApplicationImpl(@NotNull ElementType elementType, double units) {
        super(units, value -> Component.text("%,.0f ✦".formatted(units)).append(elementType.getName()));
    }
}
