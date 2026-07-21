package me.hapyl.hariant.entity.cooldown;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.util.decimal.DecimalFormat;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class HariantCooldownImpl implements HariantCooldown {
    
    private final Key key;
    private final int cooldown;
    
    HariantCooldownImpl(@NotNull Key key, final int cooldown) {
        this.key = key;
        this.cooldown = cooldown;
    }
    
    @NotNull
    @Override
    public Component format() {
        return DecimalFormat.SECONDS.format(cooldown / 20.0);
    }
    
    @NotNull
    @Override
    public Key getCooldownKey() {
        return key;
    }
    
    @Override
    public int getCooldown() {
        return cooldown;
    }
    
}
