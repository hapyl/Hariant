package me.hapyl.hariant.entity.frozen;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.util.CollectionUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Input;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public enum FrozenKey implements Named, Predicate<Input> {
    
    UP(Component.text("↑"), Input::isForward),
    LEFT(Component.text("←"), Input::isLeft),
    DOWN(Component.text("↓"), Input::isBackward),
    RIGHT(Component.text("→"), Input::isRight);
    
    private final Component name;
    private final Predicate<Input> predicate;
    
    FrozenKey(@NotNull Component name, Predicate<Input> predicate) {
        this.name = name;
        this.predicate = predicate;
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @Override
    public boolean test(@NotNull Input input) {
        return predicate.test(input);
    }
    
    @NotNull
    public static FrozenKey randomKey() {
        return CollectionUtils.randomElementOrFirst(values());
    }
    
}