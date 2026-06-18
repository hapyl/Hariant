package me.hapyl.hariant.weapon.ability;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.hariant.Colors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public enum AbilityType implements Keyed, ComponentLike {
    
    LEFT_CLICK(createComponent("ʟᴇꜰᴛ ᴄʟɪᴄᴋ")),
    RIGHT_CLICK(createComponent("ʀɪɢʜᴛ ᴄʟɪᴄᴋ")),
    SNEAK(createComponent("ꜱɴᴇᴀᴋ")),
    
    ;
    
    private final Key key;
    private final Component component;
    
    AbilityType(@NotNull Component component) {
        this.key = Key.ofString(this.name().toLowerCase());
        this.component = component;
    }
    
    @NotNull
    @Override
    public Key getKey() {
        return key;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return component;
    }
    
    private static @NotNull Component createComponent(@NotNull String string) {
        return Component.text(string, Colors.ORANGE, TextDecoration.BOLD);
    }
    
}