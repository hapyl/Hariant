package me.hapyl.hariant.entity.cooldown;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;

public interface CooldownListener {
    
    @EventLike
    void onCooldownStarted(@NotNull HariantEntity entity, int cooldown);
    
    @EventLike
    void onCooldownEnded(@NotNull HariantEntity entity);
    
}
