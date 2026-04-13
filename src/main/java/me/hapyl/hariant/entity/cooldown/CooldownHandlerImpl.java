package me.hapyl.hariant.entity.cooldown;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.entity.HariantEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Map;
import java.util.stream.Collectors;

public final class CooldownHandlerImpl implements CooldownHandler {
    
    public static boolean debugNoCooldowns = false;
    
    private final HariantEntity entity;
    private final Map<Key, CooldownInstance> cooldowns;
    
    public CooldownHandlerImpl(@NotNull HariantEntity entity) {
        this.entity = entity;
        this.cooldowns = Maps.newHashMap();
    }
    
    @Override
    public void setCooldown(@NotNull Key key, @Range(from = HariantConstants.INDEFINITE_COOLDOWN, to = Integer.MAX_VALUE) int duration) {
        if (debugNoCooldowns) {
            return;
        }
        
        // Remove the cooldown instance for 0 duration
        if (duration == 0) {
            this.cooldowns.remove(key);
        }
        else {
            this.cooldowns.put(
                    key,
                    new CooldownInstance(
                            duration == HariantConstants.INDEFINITE_COOLDOWN
                            ? HariantConstants.INDEFINITE_COOLDOWN
                            : entity.getTicksAlive() + duration
                    )
            );
        }
        
        entity.onCooldownChange(key, duration);
    }
    
    @Override
    public int getCooldownTimeLeft(@NotNull Key key) {
        final CooldownInstance instance = cooldowns.get(key);
        
        return instance != null
               ? instance.isIndefinite()
                 ? HariantConstants.INDEFINITE_COOLDOWN
                 : instance.endTick - entity.getTicksAlive()
               : 0;
    }
    
    @Override
    public boolean isOnCooldown(@NotNull Key key) {
        final CooldownInstance instance = cooldowns.get(key);
        
        return instance != null && (instance.isIndefinite() || entity.getTicksAlive() < instance.endTick);
    }
    
    @Override
    public void resetCooldowns() {
        this.cooldowns.keySet().forEach(key -> entity.onCooldownChange(key, 0));
        this.cooldowns.clear();
    }
    
    @Override
    public String toString() {
        return this.cooldowns.entrySet()
                             .stream()
                             .map(entry -> "%s = %s".formatted(entry.getKey(), entry.getValue().endTick - entity.getTicksAlive()))
                             .collect(Collectors.joining(", ", "{", "}"));
    }
    
    public static class CooldownInstance {
        private final int endTick;
        
        CooldownInstance(final int endTick) {
            this.endTick = endTick;
        }
        
        public boolean isIndefinite() {
            return endTick == HariantConstants.INDEFINITE_COOLDOWN;
        }
    }
    
}
