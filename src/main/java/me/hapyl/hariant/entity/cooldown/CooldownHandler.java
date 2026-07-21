package me.hapyl.hariant.entity.cooldown;

import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public interface CooldownHandler {
    
    @NotNull AttributeType DEFAULT_COOLDOWN_REDUCING_ATTRIBUTE = AttributeType.COOLDOWN_REDUCTION;
    
    void setCooldown(@NotNull HariantCooldown cooldown, @Range(from = HariantConstants.INDEFINITE_COOLDOWN, to = Integer.MAX_VALUE) int duration, @Nullable AttributeType cooldownReducingAttribute);
    
    default void setCooldown(@NotNull HariantCooldown cooldown, @Range(from = HariantConstants.INDEFINITE_COOLDOWN, to = Integer.MAX_VALUE) int duration) {
        this.setCooldown(cooldown, duration, DEFAULT_COOLDOWN_REDUCING_ATTRIBUTE);
    }
    
    default void setCooldown(@NotNull HariantCooldown cooldown) {
        this.setCooldown(cooldown, cooldown.getCooldown(), DEFAULT_COOLDOWN_REDUCING_ATTRIBUTE);
    }
    
    default void setIndefiniteCooldown(@NotNull HariantCooldown cooldown) {
        // Doesn't matter about the cooldown reduction, so we skip it
        this.setCooldown(cooldown, HariantConstants.INDEFINITE_COOLDOWN, null);
    }
    
    int getCooldownTimeLeft(@NotNull HariantCooldown cooldown);
    
    boolean hasCooldown(@NotNull HariantCooldown cooldown);
    
    void resetCooldowns();
    
}
