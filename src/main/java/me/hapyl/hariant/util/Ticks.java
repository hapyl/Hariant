package me.hapyl.hariant.util;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.hariant.util.decimal.Decimal;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface Ticks {
    
    /**
     * Defines the constant that may be used for "indefinite" ticks.
     *
     * <p>
     * This constant <b>must not</b> be used for indefinability check, as it is not reliable, since <b>any negative tick</b>
     * is considered as "indefinite", prefer {@link #isIndefinite(int)} instead.
     * </p>
     */
    int INDEFINITE = -1;
    
    static boolean isIndefinite(int tick) {
        return tick < 0;
    }
    
    static boolean isIndefinite(@NotNull Tick tick) {
        return tick.isNegative();
    }
    
    static boolean isIndefinite(@NotNull Decimal decimal) {
        return decimal.value() < 0;
    }
    
}