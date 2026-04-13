package me.hapyl.hariant.util.decimal;

import me.hapyl.hariant.attribute.AttributeType;
import org.jetbrains.annotations.NotNull;

public class DecimalAttributeBonusImpl extends DecimalImpl {
    DecimalAttributeBonusImpl(@NotNull AttributeType attributeType, double value) {
        super(value, attributeType);
    }
}
