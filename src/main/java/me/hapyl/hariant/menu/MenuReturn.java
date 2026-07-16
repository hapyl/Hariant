package me.hapyl.hariant.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public interface MenuReturn {
    
    @NotNull
    Component returnMenuName();
    
    @NotNull
    Menu returnMenu(@NotNull Player player);
    
    @NotNull
    static MenuReturn create(@NotNull Component name, @NotNull Supplier<Menu> supplier) {
        return new MenuReturn() {
            @NotNull
            @Override
            public Component returnMenuName() {
                return name;
            }
            
            @NotNull
            @Override
            public Menu returnMenu(@NotNull Player player) {
                return supplier.get();
            }
        };
    }
    
}
