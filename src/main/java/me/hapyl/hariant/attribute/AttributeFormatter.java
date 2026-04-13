package me.hapyl.hariant.attribute;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface AttributeFormatter {
    
    @NotNull
    Component format();
    
}
