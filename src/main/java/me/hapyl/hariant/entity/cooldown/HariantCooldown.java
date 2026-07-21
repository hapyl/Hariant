package me.hapyl.hariant.entity.cooldown;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.Attributable;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.util.ComponentFormatter;
import me.hapyl.hariant.util.decimal.DecimalFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public interface HariantCooldown extends ComponentFormatter, ComponentLike, CooldownListener {
    
    @NotNull Key getCooldownKey();
    
    int getCooldown();
    
    default void setCooldown(int cooldown) {
    }
    
    default float getCooldownSeconds() {
        return getCooldown() / 20f;
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
    @Override
    default Component asComponent() {
        return this.getCooldownFormatted();
    }
    
    @NotNull
    default Component getCooldownFormatted() {
        return DecimalFormat.SECONDS.format(this.getCooldownSeconds()).color(Colors.TICK);
    }
    
    @NotNull
    static HariantCooldown ofTicks(@NotNull Key key, final int cooldownTicks) {
        return new HariantCooldownImpl(key, cooldownTicks);
    }
    
    @NotNull
    static HariantCooldown ofSeconds(@NotNull Key key, final float cooldownSeconds) {
        return new HariantCooldownImpl(key, (int) (cooldownSeconds * 20));
    }
    
    @Override
    default void onCooldownStarted(@NotNull HariantEntity entity, final int cooldown) {
    }
    
    @Override
    default void onCooldownEnded(@NotNull HariantEntity entity) {
    }
    
    static int scaleCooldown(int cooldown, @NotNull Attributable attributable, @NotNull AttributeType attributeType) {
        final double attributeValue = attributable.getAttributes().get(attributeType);
        
        // Scaled cooldown cannot be lower than 1
        return Math.max(1, (int) (cooldown * (1 - attributeValue / 100)));
    }
    
}