package me.hapyl.hariant.event.effect;

import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;

public class HariantAttributeAddEvent extends HariantEffectEvent {
    
    public HariantAttributeAddEvent(@NotNull HariantEntity entity, @NotNull HariantEntity applier, @NotNull AttributeModifier modifier, boolean hasResisted) {
        super(entity, applier, modifier, hasResisted);
    }
    
    @Override
    public @NotNull AttributeModifier getEffect() {
        return (AttributeModifier) super.getEffect();
    }
    
}
