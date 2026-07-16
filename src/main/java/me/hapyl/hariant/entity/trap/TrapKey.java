package me.hapyl.hariant.entity.trap;

import me.hapyl.eterna.module.component.Keybind;
import me.hapyl.hariant.util.Flippable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Input;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public enum TrapKey implements Predicate<@NotNull Input>, ComponentLike, Flippable<TrapKey> {
    
    LEFT(Component.keybind(Keybind.LEFT, Style.style(TextDecoration.BOLD))) {
        @Override
        public boolean test(@NotNull Input input) {
            return input.isLeft();
        }
    },
    
    RIGHT(Component.keybind(Keybind.RIGHT, Style.style(TextDecoration.BOLD))) {
        @Override
        public boolean test(@NotNull Input input) {
            return input.isRight();
        }
    };
    
    private final Component component;
    
    TrapKey(@NotNull Component component) {
        this.component = component;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return component;
    }
    
    @Override
    public @NotNull TrapKey flipValue() {
        return this == LEFT ? RIGHT : LEFT;
    }
    
}