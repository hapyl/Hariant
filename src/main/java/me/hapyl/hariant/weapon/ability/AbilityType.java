package me.hapyl.hariant.weapon.ability;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public enum AbilityType implements Keyed, Named {
    
    LEFT_CLICK(Component.text("Left-Click")),
    RIGHT_CLICK(Component.text("Right-Click")),
    SNEAK(Component.text("Sneak")),
    
    ;
    
    private final Key key;
    private final Component name;
    
    AbilityType(@NotNull Component name) {
        this.key = Key.ofString(this.name().toLowerCase());
        this.name = name;
    }
    
    @NotNull
    @Override
    public Key getKey() {
        return key;
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
}
