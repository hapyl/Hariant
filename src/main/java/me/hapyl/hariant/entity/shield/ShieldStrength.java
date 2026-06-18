package me.hapyl.hariant.entity.shield;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.annotate.SelfReturn;
import me.hapyl.eterna.module.util.Buildable;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.element.ElementType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;

public interface ShieldStrength {
    
    double strength(@NotNull ElementType elementType);
    
    default @NotNull Component strengthFormatted(@NotNull ElementType elementType) {
        return Component.text("%.0f%%".formatted(this.strength(elementType) * 100), Colors.NUMBER);
    }
    
    static @NotNull ShieldStrength strength(double strength) {
        return elementType -> strength;
    }
    
    static @NotNull Builder builder() {
        return new Builder();
    }
    
    class Builder implements Buildable<ShieldStrength> {
        private final EnumMap<ElementType, Double> elementalStrengthMap;
        
        Builder() {
            this.elementalStrengthMap = Maps.newEnumMap(ElementType.class);
        }
        
        public @SelfReturn Builder ofElement(@NotNull ElementType elementType, double strength) {
            if (strength < HariantConstants.SHIELD_STRENGTH_MINIMUM) {
                throw new IllegalArgumentException("Shield strength cannot be lower than %s!".formatted(HariantConstants.SHIELD_STRENGTH_MINIMUM));
            }
            else if (strength > HariantConstants.SHIELD_STRENGTH_MAXIMUM) {
                throw new IllegalArgumentException("Shield strength cannot be higher than %s!".formatted(HariantConstants.SHIELD_STRENGTH_MAXIMUM));
            }
            
            elementalStrengthMap.put(elementType, strength);
            return this;
        }
        
        @Override
        public @NotNull ShieldStrength build() {
            return new ShieldStrengthImpl(elementalStrengthMap);
        }
    }
    
}
