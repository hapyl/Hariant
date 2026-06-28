package me.hapyl.hariant.talent.ultimate;

import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.component.Styled;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.util.Prefixed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public enum UltimateResourceType implements Prefixed, Named, Styled, ComponentLike, RegenerationRule {
    
    ENERGY(Component.text("✺"), Component.text("Energy"), Colors.ULTIMATE_RESOURCE_ENERGY) {
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
    
    UltimateResourceType(@NotNull Component prefix, @NotNull Component name, @NotNull TextColor color) {
        this.prefix = prefix;
        this.name = name;
        this.style = Style.style(color);
    }
    
    @NotNull
    @Override
    public Component getPrefix() {
        return prefix;
    }
    
    @NotNull
    @Override
    public Component getPrefixStyled() {
        return prefix.style(style);
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @NotNull
    @Override
    public Style getStyle() {
        return style;
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        return Component.empty()
                        .append(this.prefix.style(style))
                        .appendSpace()
                        .append(this.name.style(style));
    }
    
}
