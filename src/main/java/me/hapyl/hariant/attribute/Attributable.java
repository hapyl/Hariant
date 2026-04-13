package me.hapyl.hariant.attribute;

import me.hapyl.hariant.attribute.instance.Attributes;
import org.jetbrains.annotations.NotNull;

public interface Attributable {
    
    @NotNull
    Attributes getAttributes();
    
}
