package me.hapyl.hariant.attribute.modifier;

import me.hapyl.hariant.attribute.AttributeType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an {@link AttributeModifier} type, which defines how the modifier value is applied to the base, which follows the formula:
 *
 * <pre>{@code
 * value = (base + flat) * (1 + Σadditive) * (1 + Σmultiplicative)
 * }</pre>
 */
public enum AttributeModifierType {
    
    /**
     * Defines the {@code flat} modifier, where each value is summed together and added to the {@code base}.
     *
     * <pre>{@code
     * value = value + (n + n + n)
     * }</pre>
     */
    FLAT,
    
    /**
     * Defines the {@code additive} modifier, where each value is summed together plus {@code 1}.
     *
     * <pre>{@code
     * value = value * (1 + (n% + n% + n%))
     * }</pre>
     */
    ADDITIVE {
        @Override
        public @NotNull Component format(@NotNull AttributeType attributeType, double value) {
            // Since values for ADDITIVE and MULTIPLICATIVE are stored as decimals (0.2 -> 20%), we always
            // multiply them by 100 and format as a percentage
            return Component.text("%.0f%%".formatted(value * 100));
        }
    },
    
    /**
     * Defines the {@code multiplicative} modifier, where each value is multiplied together after adding {@code 1} to it.
     *
     * <pre>{@code
     * value = value * ((1 + n%) * (1 + n%) * (1 + n%))
     * }</pre>
     */
    MULTIPLICATIVE {
        @Override
        public @NotNull Component format(@NotNull AttributeType attributeType, double value) {
            return ADDITIVE.format(attributeType, value);
        }
    };
    
    public @NotNull Component format(@NotNull AttributeType attributeType, double value) {
        return attributeType.format(value);
    }
    
}
