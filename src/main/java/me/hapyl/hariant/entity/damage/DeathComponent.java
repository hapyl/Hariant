package me.hapyl.hariant.entity.damage;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface DeathComponent {
    
    @NotNull
    Component asDeathComponent();
    
    @NotNull
    Component asAssistComponent();
    
}
