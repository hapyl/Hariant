package me.hapyl.hariant.entity.cooldown;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.TickSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Iterator;
import java.util.Map;

public final class CooldownHandlerImpl implements CooldownHandler, Ticking {
    
    private final HariantEntity entity;
    private final Map<Key, CooldownInstance> cooldowns;
    
    public CooldownHandlerImpl(@NotNull HariantEntity entity) {
        this.entity = entity;
        this.cooldowns = Maps.newHashMap();
    }
    
    @Override
    public void setCooldown(@NotNull HariantCooldown cooldown, @Range(from = HariantConstants.INDEFINITE_COOLDOWN, to = Integer.MAX_VALUE) int duration, @Nullable AttributeType cooldownReducingAttribute) {
        final Key cooldownKey = cooldown.getCooldownKey().nonEmpty();
        
        // Remove the cooldown instance for 0 duration
        if (duration == 0) {
            cooldowns.remove(cooldownKey);
            
            this.onCooldownEnded0(cooldown);
        }
        else {
            final boolean isIndefinite = duration == HariantConstants.INDEFINITE_COOLDOWN;
            
            // If the cooldown respects the cooldown reducing attribute, and it's not indefinite, scale by it
            if (cooldownReducingAttribute != null && !isIndefinite) {
                duration = HariantCooldown.scaleCooldown(duration, entity, cooldownReducingAttribute);
            }
            
            cooldowns.put(
                    cooldownKey,
                    new CooldownInstance(cooldown, isIndefinite ? HariantConstants.INDEFINITE_COOLDOWN : entity.localTicks() + duration)
            );
            
            this.onCooldownStarted0(cooldown, duration);
        }
    }
    
    @Override
    public int getCooldownTimeLeft(@NotNull HariantCooldown cooldown) {
        final CooldownInstance instance = cooldowns.get(cooldown.getCooldownKey());
        
        return instance != null ? instance.isIndefinite() ? HariantConstants.INDEFINITE_COOLDOWN : instance.endTick - entity.localTicks() : 0;
    }
    
    @Override
    public boolean hasCooldown(@NotNull HariantCooldown cooldown) {
        final CooldownInstance instance = cooldowns.get(cooldown.getCooldownKey());
        
        return instance != null && (instance.isIndefinite() || instance.hasCooldown(entity));
    }
    
    @Override
    public void resetCooldowns() {
        this.cooldowns.values().forEach(cooldownInstance -> this.onCooldownEnded0(cooldownInstance.cooldown));
        this.cooldowns.clear();
    }
    
    @Override
    public void tick() {
        final Iterator<CooldownInstance> iterator = cooldowns.values().iterator();
        
        while (iterator.hasNext()) {
            final CooldownInstance cooldownInstance = iterator.next();
            
            // If cooldown is indefinity, skip
            if (cooldownInstance.isIndefinite()) {
                continue;
            }
            
            // If cooldown is over, remove it and call onCooldownEnded
            if (!cooldownInstance.hasCooldown(entity)) {
                iterator.remove();
                this.onCooldownEnded0(cooldownInstance.cooldown);
            }
        }
    }
    
    private void onCooldownStarted0(@NotNull HariantCooldown cooldown, int duration) {
        cooldown.onCooldownStarted(entity, duration);
        entity.onCooldownStarted(cooldown, duration);
    }
    
    private void onCooldownEnded0(@NotNull HariantCooldown cooldown) {
        cooldown.onCooldownEnded(entity);
        entity.onCooldownEnded(cooldown);
    }
    
    public static class CooldownInstance {
        
        private final HariantCooldown cooldown;
        private final int endTick;
        
        CooldownInstance(@NotNull HariantCooldown cooldown, final int endTick) {
            this.cooldown = cooldown;
            this.endTick = endTick;
        }
        
        public boolean isIndefinite() {
            return endTick == HariantConstants.INDEFINITE_COOLDOWN;
        }
        
        public boolean hasCooldown(@NotNull TickSupplier tickSupplier) {
            return tickSupplier.localTicks() < endTick;
        }
        
    }
    
}
