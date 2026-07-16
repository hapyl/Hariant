package me.hapyl.hariant.util;

import me.hapyl.hariant.Colors;
import me.hapyl.hariant.annotate.Percentage;
import me.hapyl.hariant.attribute.Attributable;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BaseChance implements ComponentFormatter, ComponentLike {
    
    private static final double DIVISOR = 100;
    
    private final double chance;
    private final Component component;
    
    private BaseChance(@Percentage(Percentage.Type.DECIMAL) double chance) {
        this.chance = chance;
        this.component = Component.text("%,.1f%% ".formatted(chance * DIVISOR), Colors.ATTRIBUTE_LUCK).append(AttributeType.LUCK.getPrefixStyled());
    }
    
    @NotNull
    @Override
    public Component format() {
        return component;
    }
    
    public double calculateChance(@Nullable Attributable attributable) {
        if (attributable == null) {
            return chance;
        }
        
        final double luck = attributable.getAttributes().get(AttributeType.LUCK);
        
        return chance * (1 + (luck / (luck + 100)));
    }
    
    public boolean chance(@NotNull HariantPlayer player) {
        return player.getRandom().chance(calculateChance(player));
    }
    
    @NotNull
    public static BaseChance baseChance(@Percentage(Percentage.Type.WHOLE_NUMBER) double chance) {
        return new BaseChance(chance / DIVISOR);
    }
    
}