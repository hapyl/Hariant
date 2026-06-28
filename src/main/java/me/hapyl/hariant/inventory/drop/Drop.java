package me.hapyl.hariant.inventory.drop;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.hariant.util.Hoverable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

public interface Drop extends Keyed, Hoverable {
    
    @Override
    @NotNull Key getKey();
    
    @NotNull Component getNameStyled();
    
    @Override
    @NotNull HoverEvent<?> createHoverEvent();
    
}