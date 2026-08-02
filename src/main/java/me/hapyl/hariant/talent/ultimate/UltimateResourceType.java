package me.hapyl.hariant.talent.ultimate;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.component.Styled;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.util.Prefixed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public enum UltimateResourceType implements Prefixed, Named, Styled, ComponentLike, RegenerationRule {
    
    ENERGY(
            Component.text("✺"),
            Component.text("Energy"),
            Colors.ULTIMATE_RESOURCE_ENERGY,
            Colors.ULTIMATE_RESOURCE_ENERGY_SECONDARY
    ) {
        @Override
        public double regeneratePassively() {
            return 0.05;
        }
        
        @Override
        public double regenerateOnElimination() {
            return 4;
        }
        
        @Override
        public double regenerateOnAssist() {
            return 2;
        }
        
        @NotNull
        @Override
        public AttributeType getEffectiveAttribute() {
            return AttributeType.ENERGY_RECHARGE;
        }
    },
    
    ;
    
    private final Component prefix;
    private final Component name;
    private final Style style;
    private final Style styleSecondary;
    
    UltimateResourceType(@NotNull Component prefix, @NotNull Component name, @NotNull TextColor color, @NotNull TextColor colorSecondary) {
        this.prefix = prefix;
        this.name = name;
        this.style = Style.style(color);
        this.styleSecondary = Style.style(colorSecondary);
    }
    
    @Override
    public @NotNull Component getPrefix() {
        return prefix;
    }
    
    @Override
    public @NotNull Component getPrefixStyled() {
        return prefix.style(style);
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public @NotNull Style getStyle() {
        return style;
    }
    
    public @NotNull Style getStyleSecondary() {
        return styleSecondary;
    }
    
    public @NotNull Style getStyleOrSecondaryStyleBasedOnCurrentTick() {
        return Hariant.currentTickMod20() ? style : styleSecondary;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return Component.empty()
                        .append(this.prefix.style(style))
                        .appendSpace()
                        .append(this.name.style(style));
    }
    
}
