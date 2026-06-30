package me.hapyl.hariant.entity.damage.tracker;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.text.Capitalizable;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.AssistSource;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DeathComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class CombatData implements DeathComponent {
    
    private final HariantEntity entity;
    
    private final Map<DamageSourceIdentity, Damage> damageDealt;
    private final Map<DamageSourceIdentity, Damage> damageTaken;
    
    private @Nullable Assist assist;
    
    CombatData(@NotNull HariantEntity entity) {
        this.entity = entity;
        this.damageDealt = Maps.newHashMap();
        this.damageTaken = Maps.newHashMap();
    }
    
    @NotNull
    public HariantEntity getEntity() {
        return entity;
    }
    
    public @Nullable Assist getAssist() {
        return assist;
    }
    
    public double totalDamage(@NotNull Type type) {
        return type.getDamageMap(this).values().stream().mapToDouble(Damage::getDamage).sum();
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
    
    public @NotNull Map<DamageSourceIdentity, Damage> getDamageMap(@NotNull Type type) {
        return type.getDamageMap(this);
    }
    
    public void assist(@NotNull AssistSource assistSource) {
        this.assist = new Assist(assistSource, System.currentTimeMillis());
    }
    
    public enum Type implements ComponentLike {
        
        OUTGOING {
            @Override
            @NotNull Map<DamageSourceIdentity, Damage> getDamageMap(@NotNull CombatData combatData) {
                return combatData.damageDealt;
            }
        },
        
        INCOMING {
            @Override
            @NotNull Map<DamageSourceIdentity, Damage> getDamageMap(@NotNull CombatData combatData) {
                return combatData.damageTaken;
            }
        };
        
        @NotNull Map<DamageSourceIdentity, Damage> getDamageMap(@NotNull CombatData combatData) {
            throw new IllegalStateException();
        }
        
        private final Component component;
        
        Type() {
            this.component = Component.text("Damage %s".formatted(Capitalizable.capitalize(this)));
        }
        
        @Override
        public @NotNull Component asComponent() {
            return component;
        }
    }
    
}