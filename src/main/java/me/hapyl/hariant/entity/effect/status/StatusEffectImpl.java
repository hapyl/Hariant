package me.hapyl.hariant.entity.effect.status;

import me.hapyl.eterna.module.component.Described;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.annotate.AutoRegisteredListener;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.effect.EffectType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

@AutoRegisteredListener
public class StatusEffectImpl implements StatusEffect {
    
    private final Key key;
    private final Component name;
    private final EffectType effectType;
    
    private Component description;
    
    StatusEffectImpl(@NotNull Key key, @NotNull Component name, @NotNull EffectType effectType) {
        this.key = key;
        this.name = name;
        this.effectType = effectType;
        this.description = Described.defaultValue();
        
        AutoRegisteredListener.Registry.register(this);
    }
    
    @Override
    @NotNull
    public Key getKey() {
        return key;
    }
    
    @NotNull
    @Override
    public Component getName() {
        return name;
    }
    
    @NotNull
    @Override
    public Component getDescription() {
        return description;
    }
    
    @Override
    public void setDescription(@NotNull Component description) {
        this.description = description;
    }
    
    @NotNull
    @Override
    public DeathMessage getDeathMessage() {
        return DeathMessage.DEFAULT;
    }
    
    @NotNull
    @Override
    public EffectType getEffectType() {
        return effectType;
    }
    
    @Override
    public void onApply(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int duration) {
    }
    
    @Override
    public void onRemove(@NotNull HariantEntity entity, @NotNull HariantEntity applier) {
    }
    
    @Override
    public void onTick(@NotNull HariantEntity entity, @NotNull HariantEntity applier, int tick) {
    }
    
}
