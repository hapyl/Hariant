package me.hapyl.hariant.util;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface Prefixed {
    
    @NotNull
    Component getPrefix();
    
    @NotNull
    default Component getPrefixStyled() {
        return this.getPrefix();
    }
    
}
