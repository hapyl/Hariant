package me.hapyl.hariant.util;

import me.hapyl.eterna.module.component.Styled;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface Prefixed {
    
    @NotNull
    Component getPrefix();
    
    @NotNull
    default Component getPrefixStyled() {
        final Component prefix = this.getPrefix();
        
        return this instanceof Styled styled ? prefix.style(styled.getStyle()) : prefix;
    }
    
}
