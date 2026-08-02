package me.hapyl.hariant.entity.effect;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.component.Styled;
import me.hapyl.hariant.Colors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

public enum EffectType implements Named, Styled {
    
    NEUTRAL(Component.text("Neutral"), Style.style(Colors.YELLOW)),
    BUFF(Component.text("Buff"), Style.style(Colors.GREEN)),
    DEBUFF(Component.text("Debuff"), Style.style(Colors.RED));
    
    private final Component name;
    private final Style style;
    
    EffectType(@NotNull Component name, @NotNull Style style) {
        this.name = name;
        this.style = style;
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public @NotNull Style getStyle() {
        return style;
    }
}
