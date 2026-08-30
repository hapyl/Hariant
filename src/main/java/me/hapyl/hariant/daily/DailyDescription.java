package me.hapyl.hariant.daily;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface DailyDescription {
    
    @NotNull Component getDescription(int goal);
    
}
