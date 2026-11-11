package me.hapyl.hariant.entity.cooldown;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.attribute.Attributable;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.TickSupplier;
import me.hapyl.hariant.util.Ticks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public void startCooldown(@NotNull Cooldown cooldown, int duration, @Nullable AttributeType cooldownReducingAttribute) {
        final Key cooldownKey = cooldown.getCooldownKey().nonEmpty();
        final boolean isIndefinite = Ticks.isIndefinite(duration);
        
        // If the cooldown respects the cooldown reducing attribute, and it's not indefinite, scale by it
        if (cooldownReducingAttribute != null && !isIndefinite) {
            duration = scaleCooldown(duration, entity, cooldownReducingAttribute);
        }
        
        this.cooldowns.put(cooldownKey, new CooldownInstance(cooldown, isIndefinite ? Ticks.INDEFINITE : entity.localTicks() + duration));
        this.onCooldownStarted0(cooldown, duration);
    }
    
    @Override
    public void stopCooldown(@NotNull Cooldown cooldown) {
        this.cooldowns.remove(cooldown.getCooldownKey());
        this.onCooldownEnded0(cooldown);
    }
    
    @Override
    public int getCooldownTimeLeft(@NotNull Cooldown cooldown) {
        final CooldownInstance instance = cooldowns.get(cooldown.getCooldownKey());
        
        return instance != null ? instance.getCooldownTimeLeft(entity) : 0;
    }
    
    @Override
    public boolean hasCooldown(@NotNull Cooldown cooldown) {
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
    
    private void onCooldownStarted0(@NotNull Cooldown cooldown, int duration) {
        cooldown.onCooldownStarted(entity, duration);
        entity.onCooldownStarted(cooldown, duration);
    }
    
    private void onCooldownEnded0(@NotNull Cooldown cooldown) {
        cooldown.onCooldownEnded(entity);
        entity.onCooldownEnded(cooldown);
    }
    
    public static int scaleCooldown(int cooldown, @NotNull Attributable attributable, @NotNull AttributeType attributeType) {
        final double attributeValue = attributable.getAttributes().get(attributeType);
        
        // Scaled cooldown cannot be lower than 1
        return Math.max(1, (int) (cooldown * (1 - attributeValue / 100)));
    }
    
    public static class CooldownInstance {
        
        private final Cooldown cooldown;
        private final int endTick;
        
        CooldownInstance(@NotNull Cooldown cooldown, final int endTick) {
            this.cooldown = cooldown;
            this.endTick = endTick;
        }
        
        public boolean isIndefinite() {
            return Ticks.isIndefinite(endTick);
        }
        
        public boolean hasCooldown(@NotNull TickSupplier tickSupplier) {
            return tickSupplier.localTicks() < endTick;
        }
        
        public int getCooldownTimeLeft(@NotNull TickSupplier tickSupplier) {
            return this.isIndefinite() ? Ticks.INDEFINITE : endTick - tickSupplier.localTicks();
        }
        
    }
    
}
