package me.hapyl.hariant.entity.cooldown;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.HariantConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public interface CooldownHandler {
    
    void setCooldown(@NotNull Key key, @Range(from = HariantConstants.INDEFINITE_COOLDOWN, to = Integer.MAX_VALUE) int duration, boolean respectCooldownReduction);
    
    default void setCooldown(@NotNull Cooldown cooldown, @Range(from = HariantConstants.INDEFINITE_COOLDOWN, to = Integer.MAX_VALUE) int duration) {
        this.setCooldown(cooldown.getCooldownKey(), duration, true);
    }
    
    default void setCooldown(@NotNull Cooldown cooldown) {
        this.setCooldown(cooldown.getCooldownKey(), cooldown.getCooldown(), true);
    }
    
    default void setIndefiniteCooldown(@NotNull Cooldown cooldown) {
        this.setCooldown(cooldown, HariantConstants.INDEFINITE_COOLDOWN);
    }
    
    default void setIndefiniteCooldown(@NotNull Key key) {
        // Doesn't matter about the cooldown reduction, so we skip it
        this.setCooldown(key, HariantConstants.INDEFINITE_COOLDOWN, false);
    }
    
    int getCooldownTimeLeft(@NotNull Key key);
    
    default int getCooldownTimeLeft(@NotNull Cooldown cooldown) {
        return this.getCooldownTimeLeft(cooldown.getCooldownKey());
    }
    
    boolean hasCooldown(@NotNull Key key);
    
    default boolean hasCooldown(@NotNull Cooldown cooldown) {
        return this.hasCooldown(cooldown.getCooldownKey());
    }
    
    void resetCooldowns();
    
}
