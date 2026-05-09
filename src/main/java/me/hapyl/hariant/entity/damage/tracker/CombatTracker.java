package me.hapyl.hariant.entity.damage.tracker;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.Streamable;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.AssistSource;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.util.Resettable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public final class CombatTracker implements Resettable, Streamable<CombatData> {
    
    private final HariantEntity entity;
    private final Map<HariantEntity, CombatData> combatData;
    
    public CombatTracker(@NotNull HariantEntity entity) {
        this.entity = entity;
        this.combatData = Maps.newHashMap();
    }
    
    public void incrementDamageDealt(@NotNull HariantEntity entity, @NotNull DamageSourceIdentity identity, double damage) {
        final CombatData data = this.getData(entity);
        
        data.totalDamageDealt.compute(identity.getKey(), (_key, _damage) -> {
            _damage = Objects.requireNonNullElseGet(_damage, () -> new Damage(identity));
            _damage.damage += damage;
            
            return _damage;
        });
    }
    
    public void assist(@NotNull AssistSource assistSource) {
        final HariantEntity source = assistSource.source();
        final CombatData data = this.getData(source);
        
        data.lastAssist = assistSource;
        data.lastAssistAt = System.currentTimeMillis();
    }
    
    @NotNull
    public Stream<? extends CombatData> assistingPlayers() {
        final double maxHealth = entity.getMaxHealth();
        final double damageThreshold = maxHealth * HariantConstants.ASSIST_DAMAGE_THRESHOLD_PERCENTAGE;
        
        return combatData.values().stream()
                         .filter(data -> {
                             // Assist Rules:
                             //  1. If damage dealt is higher than n% of entity's max health
                             //  2. If assisted in the last nL
                             
                             if (data.totalDamageDealt() >= damageThreshold) {
                                 return true;
                             }
                             else {
                                 final AssistSource lastAssist = data.getLastAssist();
                                 final long lastAssistAt = System.currentTimeMillis() - data.getLastAssistAt();
                                 
                                 return lastAssist != null && lastAssistAt < HariantConstants.ASSIST_DURATION_MILLIS;
                             }
                         });
    }
    
    @Override
    public void reset() {
        this.combatData.clear();
    }
    
    @NotNull
    @Override
    public Stream<CombatData> stream() {
        return combatData.values().stream();
    }
    
    @NotNull
    private CombatData getData(@NotNull HariantEntity entity) {
        return combatData.computeIfAbsent(entity, CombatData::new);
    }
    
}
