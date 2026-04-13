package me.hapyl.hariant.entity.effect;

import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;

public interface EffectHandler {
    
    void triggerBuff(@NotNull HariantEntity applier);
    
    void triggerDebuff(@NotNull HariantEntity applier);
    
    int countEffects(@NotNull EffectType effectType);
    
}
