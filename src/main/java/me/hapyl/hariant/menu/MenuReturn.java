package me.hapyl.hariant.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public interface MenuReturn {
    
    @NotNull
    Component menuName();
    
    @NotNull
    Menu menu(@NotNull Player player);
    
    @NotNull
    static MenuReturn create(@NotNull Component name, @NotNull Supplier<Menu> supplier) {
        return new MenuReturn() {
            @NotNull
            @Override
            public Component menuName() {
                return name;
            }
            
            @NotNull
            @Override
            public Menu menu(@NotNull Player player) {
                return supplier.get();
            }
        };
    }
    
}
