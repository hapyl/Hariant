package me.hapyl.hariant.entity.cooldown;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.util.ComponentFormatter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface Cooldown extends ComponentFormatter, CooldownListener {
    
    @NotNull Key getCooldownKey();
    
    @NotNull Tick getCooldown();
    
    default int cooldown() {
        return this.getCooldown().ticks();
    }
    
    default void setCooldown(@NotNull Tick cooldown) {
    }
    
    default boolean hasCooldown() {
        return this.getCooldown().isPositive();
    }
    
    @Override
    default @NotNull Component format() {
        return this.getCooldown().asComponent();
    }
    
    default @NotNull Component getCooldownFormatted() {
        return this.format().color(Colors.TICK);
    }
    
    @Override
    default void onCooldownStarted(@NotNull HariantEntity entity, final int cooldown) {
    }
    
    @Override
    default void onCooldownEnded(@NotNull HariantEntity entity) {
    }
    
    static @NotNull Cooldown create(@NotNull Key key, @NotNull Tick cooldown) {
        return new CooldownImpl(key, cooldown);
    }
    
}