package me.hapyl.hariant.entity.cooldown;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.HariantConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public interface CooldownHandler {
    
    void setCooldown(@NotNull Key key, @Range(from = HariantConstants.INDEFINITE_COOLDOWN, to = Integer.MAX_VALUE) int duration);
    
    default void setCooldown(@NotNull Cooldown cooldown, @Range(from = HariantConstants.INDEFINITE_COOLDOWN, to = Integer.MAX_VALUE) int duration) {
        this.setCooldown(cooldown.getCooldownKey(), duration);
    }
    
    default void setCooldown(@NotNull Cooldown cooldown) {
        this.setCooldown(cooldown.getCooldownKey(), cooldown.getCooldown());
    }
    
    default void setIndefiniteCooldown(@NotNull Cooldown cooldown) {
        this.setCooldown(cooldown, HariantConstants.INDEFINITE_COOLDOWN);
    }
    
    int getCooldownTimeLeft(@NotNull Key key);
    
    default int getCooldownTimeLeft(@NotNull Cooldown cooldown) {
        return this.getCooldownTimeLeft(cooldown.getCooldownKey());
    }
    
    boolean isOnCooldown(@NotNull Key key);
    
    default boolean isOnCooldown(@NotNull Cooldown cooldown) {
        return this.isOnCooldown(cooldown.getCooldownKey());
    }
    
    void resetCooldowns();
    
}
