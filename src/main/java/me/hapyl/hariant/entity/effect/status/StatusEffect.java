package me.hapyl.hariant.entity.effect.status;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.component.Named;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.effect.Effect;
import me.hapyl.hariant.entity.effect.EffectType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public interface StatusEffect extends Effect, Named, Described, DamageSourceIdentity, ComponentLike {
    
    @Override
    @NotNull Key getKey();
    
    @Override
    @NotNull EffectType getEffectType();
    
    @Override
    void onApply(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int duration);
    
    @Override
    void onRemove(@NotNull HariantEntity entity, @NotNull HariantEntity applier);
    
    @Override
    void onTick(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int tick, int duration);
    
    @Override
    @NotNull DeathMessage getDeathMessage();
    
    @Override
    @NotNull Component getName();
    
    @Override
    @NotNull Component getDescription();
    
    @Override
    default @NotNull Component asComponent() {
        return getName();
    }
    
}