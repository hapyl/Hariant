package me.hapyl.hariant.entity.cooldown;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.util.ComponentFormatter;
import me.hapyl.hariant.util.decimal.DecimalFormat;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface Cooldown extends ComponentFormatter {
    
    @NotNull
    Key getCooldownKey(); // We technically can extend Keyed and make this getKey(), but we might need to use a separate key for cooldowns and object
    
    int getCooldown();
    
    default float getCooldownSeconds() {
        return getCooldown() / 20f;
    }
    
    default void setCooldown(int cooldown) {
    }
    
    default void setCooldownSeconds(float seconds) {
        this.setCooldown((int) (seconds * 20));
    }
    
    default boolean hasCooldown() {
        return getCooldown() > 0;
    }
    
    @NotNull
    @Override
    default Component format() {
        return DecimalFormat.SECONDS.format(this.getCooldownSeconds());
    }
    
    @NotNull
    default Component getCooldownFormatted() {
        return DecimalFormat.SECONDS.format(this.getCooldownSeconds()).color(Colors.FORMAT_TICK);
    }
    
    @NotNull
    static Cooldown ofTicks(@NotNull Key key, final int cooldownTicks) {
        return new CooldownImpl(key, cooldownTicks);
    }
    
    @NotNull
    static Cooldown ofSeconds(@NotNull Key key, final float cooldownSeconds) {
        return new CooldownImpl(key, (int) cooldownSeconds * 20);
    }
    
}
