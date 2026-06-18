package me.hapyl.hariant.entity.damage.tracker;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Streamable;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.AssistSource;
import me.hapyl.hariant.entity.damage.DeathComponent;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Stream;

public class CombatData implements DeathComponent, Streamable<Damage> {
    
    protected final HariantEntity entity;
    protected final Map<Key, Damage> totalDamageDealt;
    
    @Nullable
    protected AssistSource lastAssist;
    protected long lastAssistAt;
    
    CombatData(@NotNull HariantEntity entity) {
        this.entity = entity;
        this.totalDamageDealt = Maps.newHashMap();
    }
    
    @NotNull
    public HariantEntity getEntity() {
        return entity;
    }
    
    @Nullable
    public AssistSource getLastAssist() {
        return lastAssist;
    }
    
    public long getLastAssistAt() {
        return lastAssistAt;
    }
    
    public double totalDamageDealt() {
        return totalDamageDealt.values().stream().mapToDouble(Damage::getDamage).sum();
    }
    
    @NotNull
    @Override
    public Component asDeathComponent() {
        return entity.asDeathComponent();
    }
    
    @NotNull
    @Override
    public Component asAssistComponent() {
        return entity.asAssistComponent();
    }
    
    @NotNull
    @Override
    public Stream<Damage> stream() {
        return totalDamageDealt.values().stream();
    }
    
}