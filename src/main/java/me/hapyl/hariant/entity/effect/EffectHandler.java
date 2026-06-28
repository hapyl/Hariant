package me.hapyl.hariant.entity.effect;

import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.event.HariantEffectEvent;
import org.jetbrains.annotations.NotNull;

public interface EffectHandler {
    
    /**
     * Triggers the given {@link Effect}.
     *
     * @param applier - The effect applier.
     * @param effect  - The effect to trigger.
     * @return {@code true} if the effect was cancelled, either via {@link AttributeType#EFFECT_RESISTANCE} or {@link HariantEffectEvent}; {@code false} otherwise.
     */
    boolean triggerEffect(@NotNull HariantEntity applier, @NotNull Effect effect);
    
    int countEffects(@NotNull EffectType effectType);
    
}
