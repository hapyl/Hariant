package me.hapyl.hariant.talent.energy;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.TickSupplier;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.util.Resettable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class EnergyRegenerationRules implements Resettable {
    
    private final HariantPlayer player;
    private final Map<Key, FulfilmentState> states;
    
    public EnergyRegenerationRules(@NotNull HariantPlayer player) {
        this.player = player;
        this.states = Maps.newHashMap();
    }
    
    public boolean test(@NotNull EnergyRegenerationRule regenerationRule) {
        final Fulfilment fulfilment = regenerationRule.fulfilment();
        
        // If both cooldown and threshold are 0, we simply return true
        final int cooldown = fulfilment.cooldown().ticks();
        final int threshold = fulfilment.threshold();
        
        if (cooldown == 0 && threshold == 0) {
            return true;
        }
        
        // If state doesn't exist, return true and create it
        final Key ruleKey = regenerationRule.getKey();
        final FulfilmentState state = states.get(ruleKey);
        
        if (state == null) {
            states.put(ruleKey, new FulfilmentState(player));
            return true;
        }
        
        final int localTicks = player.localTicks();
        
        // If threshold is hit, reset the threshold
        if (threshold > 0 && state.threshold >= threshold) {
            state.threshold = 0;
            return true;
        }
        // If cooldown is reached, reset both cooldown and threshold
        else if (state.tick > 0 && localTicks - state.tick > cooldown) {
            state.threshold = 0;
            state.tick = 0;
            return true;
        }
        
        state.tick = localTicks;
        state.threshold++;
        
        return false;
    }
    
    @Override
    public void reset() {
        this.states.clear();
    }
    
    public static class FulfilmentState {
        
        private int tick;
        private int threshold;
        
        FulfilmentState(@NotNull TickSupplier tickSupplier) {
            this.tick = tickSupplier.localTicks();
        }
        
    }
    
}