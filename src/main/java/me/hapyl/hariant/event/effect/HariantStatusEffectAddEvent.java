package me.hapyl.hariant.event.effect;

import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.effect.status.StatusEffectType;
import org.jetbrains.annotations.NotNull;

public class HariantStatusEffectAddEvent extends HariantEffectEvent {
    
    public HariantStatusEffectAddEvent(@NotNull HariantEntity entity, @NotNull HariantEntity applier, @NotNull StatusEffectType effect, boolean hasResisted) {
        super(entity, applier, effect, hasResisted);
    }
    
    @Override
    public @NotNull StatusEffectType getEffect() {
        return (StatusEffectType) super.getEffect();
    }
}
