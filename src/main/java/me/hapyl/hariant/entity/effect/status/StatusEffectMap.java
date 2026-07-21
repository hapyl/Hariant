package me.hapyl.hariant.entity.effect.status;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.event.effect.HariantEffectEvent;
import me.hapyl.hariant.event.effect.HariantStatusEffectAddEvent;
import me.hapyl.hariant.event.effect.HariantStatusEffectRemoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public final class StatusEffectMap implements Ticking, StatusEffectHandler {
    
    private final HariantEntity entity;
    private final Map<StatusEffectType, StatusEffectInstance> effectMap;
    
    public StatusEffectMap(@NotNull HariantEntity entity) {
        this.entity = entity;
        this.effectMap = Maps.newEnumMap(StatusEffectType.class);
    }
    
    @NotNull
    public HariantEntity getEntity() {
        return entity;
    }
    
    @Override
    public void tick() {
        final Iterator<StatusEffectInstance> iterator = effectMap.values().iterator();
        
        while (iterator.hasNext()) {
            final StatusEffectInstance next = iterator.next();
            
            next.tick();
            
            if (next.isOver()) {
                next.remove();
                iterator.remove();
            }
        }
    }
    
    @Override
    public void addEffect(@NotNull StatusEffectType effect, int duration, @NotNull HariantEntity applier) {
        if (HariantEffectEvent.callEvent(entity, applier, effect, HariantStatusEffectAddEvent::new)) {
            return;
        }
        
        final StatusEffectInstance previousEffect = effectMap.get(effect);
        
        // If previous effect exists, we simply mutate it
        if (previousEffect != null) {
            previousEffect.mutate(duration, applier);
        }
        // Otherwise instantiate new effect
        else {
            final StatusEffectInstance effectInstance = new StatusEffectInstance(effect, entity, applier, duration);
            effectMap.put(effect, effectInstance);
            
            // Call event
            effect.onApply(entity, applier, duration);
        }
    }
    
    @Override
    public void removeEffect(@NotNull StatusEffectType effect) {
        final StatusEffectInstance previousEffect = effectMap.remove(effect);
        
        if (previousEffect != null) {
            previousEffect.remove();
            
            // Call event
            new HariantStatusEffectRemoveEvent(entity, previousEffect.getEffect()).callEvent();
        }
    }
    
    @Override
    public void resetEffects() {
        effectMap.values().forEach(StatusEffectInstance::remove);
        effectMap.clear();
    }
    
    @Override
    public boolean hasEffect(@NotNull StatusEffectType effect) {
        return effectMap.containsKey(effect);
    }
    
    @NotNull
    @Override
    public Optional<StatusEffectInstance> getEffect(@NotNull StatusEffectType effect) {
        return Optional.ofNullable(effectMap.get(effect));
    }
    
    @NotNull
    @Override
    public Stream<StatusEffectInstance> getEffects() {
        return effectMap.values().stream();
    }
    
}
