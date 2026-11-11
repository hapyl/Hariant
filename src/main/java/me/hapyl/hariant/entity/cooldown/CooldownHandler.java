package me.hapyl.hariant.entity.cooldown;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.util.Ticks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CooldownHandler {
    
    @NotNull AttributeType DEFAULT_COOLDOWN_REDUCING_ATTRIBUTE = AttributeType.COOLDOWN_REDUCTION;
    
    void startCooldown(@NotNull Cooldown cooldown, int duration, @Nullable AttributeType cooldownReducingAttribute);
    
    default void startCooldown(@NotNull Cooldown cooldown, @NotNull Tick tick, @Nullable AttributeType cooldownReducingAttribute) {
        this.startCooldown(cooldown, tick.ticks(), cooldownReducingAttribute);
    }
    
    default void startCooldown(@NotNull Cooldown cooldown, int duration) {
        this.startCooldown(cooldown, duration, DEFAULT_COOLDOWN_REDUCING_ATTRIBUTE);
    }
    
    default void startCooldown(@NotNull Cooldown cooldown, @NotNull Tick duration) {
        this.startCooldown(cooldown, duration.ticks(), DEFAULT_COOLDOWN_REDUCING_ATTRIBUTE);
    }
    
    default void startCooldown(@NotNull Cooldown cooldown) {
        this.startCooldown(cooldown, cooldown.getCooldown(), DEFAULT_COOLDOWN_REDUCING_ATTRIBUTE);
    }
    
    default void startIndefiniteCooldown(@NotNull Cooldown cooldown) {
        // Doesn't matter about the cooldown reduction, so we skip it
        this.startCooldown(cooldown, Ticks.INDEFINITE, null);
    }
    
    void stopCooldown(@NotNull Cooldown cooldown);
    
    int getCooldownTimeLeft(@NotNull Cooldown cooldown);
    
    boolean hasCooldown(@NotNull Cooldown cooldown);
    
    void resetCooldowns();
    
}