package me.hapyl.hariant.hero;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.component.Styled;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.util.Prefixed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

public enum Affiliation implements Prefixed, Named, Described, Styled, ComponentLike {
    
    NONE(
            Component.text("❌"),
            Component.text("None"),
            Component.text("This hero is not affiliated with anything."),
            Style.style(Colors.ERROR)
    ),
    
    THE_KINGDOM(
            Component.text("🏰"),
            Component.text("The Kingdom"),
            Component.text("A royal kingdom, that is the capital."),
            Style.style(Colors.THE_KINGDOM)
    ),
    
    THE_WITHERS(
            Component.text("👾"),
            Component.text("The Withers"),
            Component.text("An ancient race of withers, who bear hatred towards humanity."),
            Style.style(Colors.THE_WITHERS)
    ),
    
    THE_JUNGLE(
            Component.text("🌺"),
            Component.text("The Jungle"),
            Component.text("A massive jungle filled with trees and bandits."),
            Style.style(Colors.THE_JUNGLE)
    ),
    
    MERCENARY(
            Component.text("\uD83D\uDD74"),
            Component.text("The Mercenaries"),
            Component.text("A group of mercenaries and bounty hunters."),
            Style.style(Colors.MERCENARY)
    ),
    
    CHATEAU(
            Component.text("🦇"),
            Component.text("Château"),
            Component.text("A mansion filled with vampires."),
            Style.style(Colors.BLOOD)
    ),
    
    THE_SPACE(
            Component.text("🌌"),
            Component.text("The Space"),
            Component.text("\"The galaxy is vast beyond compare.\""),
            Style.style(Colors.THE_SPACE)
    ),
    
    HELL(
            Component.text("⛓"),
            Component.text("Hell"),
            Component.text("A barren land of the underworld."),
            Style.style(Colors.HELL)
    ),
    
    ;
    
    private final Component prefix;
    private final Component name;
    private final Component description;
    private final Style style;
    
    private final Component component;
    
    Affiliation(@NotNull Component prefix, @NotNull Component name, @NotNull Component description, @NotNull Style style) {
        this.style = style;
        this.prefix = prefix;
        this.name = name;
        this.description = description;
        this.component = Component.empty()
                                  .append(prefix.style(style))
                                  .appendSpace()
                                  .append(name.style(style));
    }
    
    @NotNull
    @Override
    public Component getPrefix() {
        return prefix;
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @NotNull
    @Override
    public Component getDescription() {
        return description;
    }
    
    @NotNull
    @Override
    public Style getStyle() {
        return style;
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        return component;
    }
}
