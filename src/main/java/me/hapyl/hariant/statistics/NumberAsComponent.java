package me.hapyl.hariant.statistics;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface NumberAsComponent {
    
    @NotNull Component asComponent(@NotNull Number number);
    
}
