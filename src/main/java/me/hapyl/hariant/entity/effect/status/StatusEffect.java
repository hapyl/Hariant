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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface StatusEffect extends Effect, Named, Described, DamageSourceIdentity {
    
    @Override
    @NotNull
    Key getKey();
    
    @NotNull
    @Override
    EffectType getEffectType();
    
    @Override
    void onApply(@NotNull HariantEntity entity, @Nullable HariantEntity applier);
    
    @Override
    void onRemove(@NotNull HariantEntity entity, @Nullable HariantEntity applier);
    
    @Override
    void onTick(@NotNull HariantEntity entity, @Nullable HariantEntity applier, int tick);
    
    @Override
    @NotNull
    DeathMessage getDeathMessage();
    
    @Override
    @NotNull
    Component getName();
    
    @NotNull
    @Override
    Component getDescription();
    
}
