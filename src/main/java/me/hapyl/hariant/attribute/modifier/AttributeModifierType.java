package me.hapyl.hariant.attribute.modifier;

import me.hapyl.hariant.util.decimal.DecimalFormat;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an {@link AttributeModifier} type, which defines how the modifier value is applied to the base, which follows the formula:
 *
 * <pre>{@code
 * value = (base + flat) * (1 + Σadditive) * (1 + Σmultiplicative)
 * }</pre>
 */
public enum AttributeModifierType implements DecimalFormat {
    
    /**
     * Defines the {@code flat} modifier, where each value is summed together and added to the {@code base}.
     *
     * <pre>{@code
     * value = value + (n + n + n)
     * }</pre>
     */
    FLAT {
        @Override
        @NotNull
        public Component format(double value) {
            return Component.text("%,.0f".formatted(value));
        }
    },
    
    /**
     * Defines the {@code additive} modifier, where each value is summed together plus {@code 1}.
     *
     * <pre>{@code
     * value = value * (1 + (n% + n% + n%))
     * }</pre>
     */
    ADDITIVE {
        @NotNull
        @Override
        public Component format(double value) {
            return Component.text("%.2f%%".formatted(value * 100));
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
        @NotNull
        @Override
        public Component format(double value) {
            return ADDITIVE.format(value);
        }
    }
    
}
